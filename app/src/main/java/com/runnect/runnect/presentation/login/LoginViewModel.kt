package com.runnect.runnect.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestLogin
import com.runnect.runnect.data.dto.response.LoginDTO
import com.runnect.runnect.domain.LoginRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {


    val loginResult = MutableLiveData<LoginDTO>()
    val errorMessage = MutableLiveData<String>()

    private val _loginState = MutableLiveData<UiState>(UiState.Empty)
    val loginState: LiveData<UiState>
        get() = _loginState


    fun postLogin(request: RequestLogin) {
        viewModelScope.launch {
            runCatching {
                _loginState.value = UiState.Loading
                loginRepository.postLogin(
                    RequestLogin(
                        request.token, request.provider
                    )
                )
            }.onSuccess {
                loginResult.value = it
                _loginState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _loginState.value = UiState.Failure
            }
        }
    }

}