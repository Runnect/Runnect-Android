package com.runnect.runnect.presentation.mypage.setting.accountinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySettingAccountInfoViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    private val _withdrawalState = MutableLiveData<UiState>()
    val withdrawalState: LiveData<UiState>
        get() = _withdrawalState
    val errorMessage = MutableLiveData<String>()

    fun deleteUser() {
        viewModelScope.launch {
            runCatching {
                _withdrawalState.value = UiState.Loading
                userRepository.deleteUser()
            }.onSuccess {
                _withdrawalState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _withdrawalState.value = UiState.Failure
            }
        }
    }
}