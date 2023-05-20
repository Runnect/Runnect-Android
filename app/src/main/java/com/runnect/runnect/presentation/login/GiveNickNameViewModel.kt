package com.runnect.runnect.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiveNickNameViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    val nickName = MutableLiveData<String>()
    val uiState: LiveData<UiState>
        get() = _uiState
    private val _uiState = MutableLiveData<UiState>()
    val statusCode: LiveData<Int>
        get() = _statusCode
    private val _statusCode = MutableLiveData<Int>()

    fun updateNickName() {
        viewModelScope.launch {
            runCatching {
                _uiState.value = UiState.Loading
                userRepository.updateNickName(RequestUpdateNickName(nickName.value.toString()))
            }.onSuccess {
                _uiState.value = UiState.Success
            }.onFailure {
                _statusCode.value = REDUNDANT_NICKNAME_ERROR
                _uiState.value = UiState.Failure
            }
        }
    }

    companion object {
        const val REDUNDANT_NICKNAME_ERROR = 400
    }
}