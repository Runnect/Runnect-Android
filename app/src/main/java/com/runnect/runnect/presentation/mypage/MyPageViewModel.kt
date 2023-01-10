package com.runnect.runnect.presentation.mypage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    val nickName: MutableLiveData<String> = MutableLiveData<String>()
    val stamp: MutableLiveData<Int> = MutableLiveData<Int>(R.drawable.mypage_img_stamp_lock)
    val level: MutableLiveData<Int> = MutableLiveData<Int>()
    val levelPercent: MutableLiveData<Int> = MutableLiveData<Int>()

    fun getUserInfo() {
        viewModelScope.launch {
            runCatching {
                userRepository.getUserInfo()
            }.onSuccess {
                nickName.value = it.data.user.nickname
                stamp.value = getProfileStamp(it.data.user.latestStamp)
                level.value = it.data.user.level
                levelPercent.value = it.data.user.levelPercent
            }.onFailure {
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
            else -> R.drawable.mypage_img_stamp_lock //CSPR0 프로필 이미지로 변경 예정
        }
    }
}