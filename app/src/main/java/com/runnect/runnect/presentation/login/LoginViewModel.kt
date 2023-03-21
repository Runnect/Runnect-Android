package com.runnect.runnect.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.api.KApiLogin
import com.runnect.runnect.data.model.RequestPostLoginDto
import com.runnect.runnect.data.model.ResponsePostLoginDto
import com.runnect.runnect.presentation.state.UiState
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    val service = KApiLogin.ServicePool.loginService

    val loginResult = MutableLiveData<ResponsePostLoginDto>()
    val errorMessage = MutableLiveData<String>()

    private val _loginState = MutableLiveData<UiState>(UiState.Empty)
    val loginState: LiveData<UiState>
        get() = _loginState


    fun postLogin(request: RequestPostLoginDto) {
        viewModelScope.launch {
            runCatching {
                _loginState.value = UiState.Loading
                service.postLogin(RequestPostLoginDto(
                    request.token, request.provider
                ))
            }.onSuccess {
                loginResult.value = it.body()
                _loginState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _loginState.value = UiState.Failure
            }
        }
    }

}