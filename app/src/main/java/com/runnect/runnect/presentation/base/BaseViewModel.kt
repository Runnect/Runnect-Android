package com.runnect.runnect.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.domain.common.toLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.net.SocketException
import java.net.UnknownHostException

abstract class BaseViewModel : ViewModel() {

    sealed interface EventState {
        object Empty : EventState
        data class ShowToast(val message: String) : EventState
        data class ShowSnackBar(val message: String) : EventState
        data class NetworkError(val message: String) : EventState
        data class UnknownError(val message: String) : EventState
    }

    private val _eventState: MutableSharedFlow<EventState> = MutableSharedFlow()
    val eventState: SharedFlow<EventState> = _eventState.asSharedFlow()

    protected fun sendEvent(event: EventState) {
        viewModelScope.launch {
            _eventState.emit(event)
        }
    }

    open val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.tag(throwable::class.java.simpleName).e(throwable)

        when (throwable) {
            is SocketException,
            is HttpException,
            is UnknownHostException -> {
                sendEvent(
                    EventState.NetworkError(throwable.toLog())
                )
                Timber.e(throwable)
            }
            else -> {
                sendEvent(
                    EventState.UnknownError(throwable.toLog())
                )
                Timber.e(throwable)
            }
        }
    }

    fun ViewModel.launchWithHandler(
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        exceptionHandler: CoroutineExceptionHandler = this@BaseViewModel.exceptionHandler,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(dispatcher + exceptionHandler, start, block)
    }
}