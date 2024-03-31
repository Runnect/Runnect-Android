package com.runnect.runnect.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.LoginDTO
import com.runnect.runnect.data.dto.request.RequestPostLogin
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.repository.LoginRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    val loginResult = MutableLiveData<LoginDTO>()
    val errorMessage = MutableLiveData<String>()

    private val _loginState = MutableLiveData<UiState>(UiState.Empty)
    val loginState: LiveData<UiState>
        get() = _loginState

    fun postLogin(request: RequestPostLogin, logEvent: (() -> Unit)? = null) = launchWithHandler {
        val requestPostLogin = RequestPostLogin(
            token = request.token,
            provider = request.provider
        )

        loginRepository.postLogin(requestPostLogin)
            .onStart {
                _loginState.value = UiState.Loading
            }.collectResult(
                onSuccess = {
                    loginResult.value = it
                    _loginState.value = UiState.Success
                    logEvent?.invoke()
                },
                onFailure = {
                    errorMessage.value = it.toLog()
                    _loginState.value = UiState.Failure
                }
            )
    }
}