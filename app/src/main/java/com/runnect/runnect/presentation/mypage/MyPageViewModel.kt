package com.runnect.runnect.presentation.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.R
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    val nickName: MutableLiveData<String> = MutableLiveData<String>()
    val stampId: MutableLiveData<String> = MutableLiveData<String>(STAMP_LOCK)
    val level: MutableLiveData<String> = MutableLiveData<String>()
    val levelPercent: MutableLiveData<Int> = MutableLiveData<Int>()

    private val _userInfoState = MutableLiveData<UiState>(UiState.Loading)
    val userInfoState: LiveData<UiState>
        get() = _userInfoState

    val errorMessage = MutableLiveData<String>()

    fun getUserInfo() {
        viewModelScope.launch {
            runCatching {
                _userInfoState.value = UiState.Loading
                userRepository.getUserInfo()
            }.onSuccess {
                nickName.value = it.data.user.nickname
                stampId.value = it.data.user.latestStamp
                level.value = it.data.user.level.toString()
                levelPercent.value = it.data.user.levelPercent
                _userInfoState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _userInfoState.value = UiState.Failure
            }
        }
    }

    companion object {
        const val STAMP_LOCK = "lock"
    }
}