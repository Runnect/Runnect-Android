package com.runnect.runnect.developer.presentation

import com.runnect.runnect.BuildConfig
import com.runnect.runnect.developer.domain.ServerStatusRepository
import com.runnect.runnect.domain.common.getCode
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class RunnectDeveloperViewModel @Inject constructor(
    private val serverStatusRepository: ServerStatusRepository
) : BaseViewModel() {

    private val _prodStatus: MutableSharedFlow<ServerState> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val prodStatus: SharedFlow<ServerState> = _prodStatus.asSharedFlow()

    private val _testStatus: MutableSharedFlow<ServerState> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val testStatus: SharedFlow<ServerState> = _testStatus.asSharedFlow()

    fun checkProdServerStatus() {
        val prodServerUrl = "${BuildConfig.RUNNECT_PROD_URL}/actuator/health"
        checkServerStatus(prodServerUrl, _prodStatus)
    }

    fun checkTestServerStatus() {
        val testServerUrl = "${BuildConfig.RUNNECT_NODE_URL}/actuator/health"
        checkServerStatus(testServerUrl, _testStatus)
    }

    private fun checkServerStatus(
        serverUrl: String,
        state: MutableSharedFlow<ServerState>
    ) = launchWithHandler {
        serverStatusRepository.checkServerStatus(serverUrl)
            .flowOn(Dispatchers.IO)
            .onStart {
                state.emit(ServerState.Checking)
            }.catch {
                state.tryEmit(ServerState.Unknown)
            }.collectResult(
                onSuccess = {
                    state.tryEmit(ServerState.Running)
                },
                onFailure = {
                    when (it.getCode()) {
                        503 -> ServerState.Degraded
                        else -> ServerState.Error
                    }.let(state::tryEmit)
                }
            )
    }

    sealed interface ServerState {
        object Running : ServerState
        object Degraded : ServerState
        object Error : ServerState
        object Unknown : ServerState
        object Checking : ServerState
    }
}