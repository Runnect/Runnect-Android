package com.runnect.runnect.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class GiveNickNameViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    val nickName = MutableLiveData<String>()
    val uiState: LiveData<UiState>
        get() = _uiState
    private val _uiState = MutableLiveData<UiState>()
    val statusCode: LiveData<Int>
        get() = _statusCode
    private val _statusCode = MutableLiveData<Int>()

    fun updateNickName() = launchWithHandler {
        val requestPatchNickName = RequestPatchNickName(nickName.value.toString())

        userRepository.updateNickName(requestPatchNickName)
            .onStart {
                _uiState.value = UiState.Loading
            }.collectResult(
                onSuccess = {
                    _uiState.value = UiState.Success
                },
                onFailure = {
                    _statusCode.value = REDUNDANT_NICKNAME_ERROR
                    _uiState.value = UiState.Failure
                }
            )
    }

    companion object {
        const val REDUNDANT_NICKNAME_ERROR = 400
    }
}