package com.runnect.runnect.presentation.mypage.editname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageEditNameViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    val nickName = MutableLiveData<String>()
    val uiState: LiveData<UiState>
        get() = _uiState
    private val _uiState = MutableLiveData<UiState>()
    val profileImg: MutableLiveData<Int> = MutableLiveData<Int>(R.drawable.user_profile_basic)
    val statusCode: LiveData<Int>
        get() = _statusCode
    private val _statusCode = MutableLiveData<Int>()

    fun setNickName(nickName: String) {
        this.nickName.value = nickName
    }

    fun setProfileImg(profileImg: Int) {
        this.profileImg.value = profileImg
    }

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