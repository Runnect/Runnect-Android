package com.runnect.runnect.presentation.mypage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class MyPageViewModel : ViewModel() {
    val nickName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
@HiltViewModel
class MyPageViewModel @Inject constructor(private val userRepository: UserRepository) :
    val stamp: MutableLiveData<Int> = MutableLiveData<Int>(R.drawable.mypage_img_stamp_lock)
                stamp.value = getProfileStamp(it.data.user.latestStamp)
}