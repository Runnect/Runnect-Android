package com.runnect.runnect.presentation.mypage.editname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class MyPageEditNameViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {
    val nickName = MutableLiveData<String>()

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState>
        get() = _uiState

    val profileImgResId: MutableLiveData<Int> = MutableLiveData<Int>(R.drawable.user_profile_basic)

    val statusCode: LiveData<Int>
        get() = _statusCode
    private val _statusCode = MutableLiveData<Int>()

    fun setNickName(nickName: String) {
        this.nickName.value = nickName
    }

    fun setProfileImg(profileImgResId: Int) {
        this.profileImgResId.value = profileImgResId
    }

    fun updateNickName() = launchWithHandler {
        val requestPatchNickName = RequestPatchNickName(
            nickname = nickName.value.toString()
        )

        userRepository.updateNickName(requestPatchNickName)
            .onStart {
                _uiState.value = UiState.Loading
            }.collectResult(
                onSuccess = {
                    _uiState.value = UiState.Success
                },
                onFailure = {
                    _uiState.value = UiState.Failure
                    _statusCode.value = REDUNDANT_NICKNAME_ERROR
                }
            )
    }

    companion object {
        const val REDUNDANT_NICKNAME_ERROR = 400
    }
}