package com.runnect.runnect.presentation.mypage.setting.accountinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class MySettingAccountInfoViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _withdrawalState = MutableLiveData<UiState>()
    val withdrawalState: LiveData<UiState>
        get() = _withdrawalState

    val errorMessage = MutableLiveData<String>()

    fun deleteUser() = launchWithHandler {
        userRepository.deleteUser()
            .onStart {
                _withdrawalState.value = UiState.Loading
            }.collectResult(
                onSuccess = {
                    _withdrawalState.value = UiState.Success
                },
                onFailure = {
                    errorMessage.value = it.toLog()
                    _withdrawalState.value = UiState.Failure
                }
            )
    }
}