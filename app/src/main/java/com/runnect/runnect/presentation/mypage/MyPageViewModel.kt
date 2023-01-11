package com.runnect.runnect.presentation.mypage

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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    val nickName: MutableLiveData<String> = MutableLiveData<String>()
    val stamp: MutableLiveData<Int> = MutableLiveData<Int>(R.drawable.mypage_img_stamp_lock)
    val level: MutableLiveData<String> = MutableLiveData<String>()
    val levelPercent: MutableLiveData<Int> = MutableLiveData<Int>()

    private val _userInfoState = MutableLiveData<UiState>(UiState.Loading)
    val userInfoState: LiveData<UiState>
        get() = _userInfoState

    fun getUserInfo() {
        viewModelScope.launch {
            runCatching {
                _userInfoState.value = UiState.Loading
                userRepository.getUserInfo()
            }.onSuccess {
                nickName.value = it.data.user.nickname
                stamp.value = getProfileStamp(it.data.user.latestStamp)
                level.value = it.data.user.level.toString()
                levelPercent.value = it.data.user.levelPercent
                _userInfoState.value = UiState.Success
            }.onFailure {
                _userInfoState.value = UiState.Failure
            }
        }
    }

    fun updateNickName() {
        viewModelScope.launch {
            runCatching {
                userRepository.updateNickName(RequestUpdateNickName(nickName.value.toString()))
            }.onSuccess {
            }.onFailure {
                Timber.d("${it.message}")
            }
        }
    }

    private fun getProfileStamp(stamp: String): Int {
        return when (stamp) {
            "c1" -> R.drawable.mypage_img_stamp_c1
            "c2" -> R.drawable.mypage_img_stamp_c2
            "c3" -> R.drawable.mypage_img_stamp_c3
            "s1" -> R.drawable.mypage_img_stamp_s1
            "s2" -> R.drawable.mypage_img_stamp_s2
            "s3" -> R.drawable.mypage_img_stamp_s3
            "u1" -> R.drawable.mypage_img_stamp_u1
            "u2" -> R.drawable.mypage_img_stamp_u2
            "u3" -> R.drawable.mypage_img_stamp_u3
            "r1" -> R.drawable.mypage_img_stamp_r1
            "r2" -> R.drawable.mypage_img_stamp_r2
            "r3" -> R.drawable.mypage_img_stamp_r3
            else -> R.drawable.user_profile_basic //CSPR0 프로필 이미지로 변경 예정
        }
    }
}