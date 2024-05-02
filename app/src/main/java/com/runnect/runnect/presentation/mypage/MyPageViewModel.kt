package com.runnect.runnect.presentation.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.R
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    val nickName: MutableLiveData<String> = MutableLiveData<String>()
    val stampId: MutableLiveData<String> = MutableLiveData<String>(STAMP_LOCK)
    val profileImgResId: MutableLiveData<Int> = MutableLiveData<Int>(R.drawable.user_profile_basic)
    val level: MutableLiveData<String> = MutableLiveData<String>()
    val levelPercent: MutableLiveData<Int> = MutableLiveData<Int>()
    val email: MutableLiveData<String> = MutableLiveData<String>()

    private val _userInfoState = MutableLiveData<UiState>(UiState.Loading)
    val userInfoState: LiveData<UiState>
        get() = _userInfoState

    val errorMessage = MutableLiveData<String>()
    fun setNickName(nickName: String) {
        this.nickName.value = nickName
    }

    fun setProfileImg(profileImgResId: Int) {
        this.profileImgResId.value = profileImgResId
    }

    fun getUserInfo() = launchWithHandler {
        userRepository.getUserInfo()
            .onStart {
                _userInfoState.value = UiState.Loading
            }.collectResult(
                onSuccess = { user ->
                    user.let {
                        level.value = it.level.toString()
                        nickName.value = it.nickname
                        stampId.value = it.latestStamp
                        levelPercent.value = it.levelPercent
                        email.value = it.email
                    }

                    _userInfoState.value = UiState.Success
                },
                onFailure = {
                    errorMessage.value = it.toLog()
                    _userInfoState.value = UiState.Failure
                }
            )
    }

    companion object {
        const val STAMP_LOCK = "lock"
    }
}