package com.runnect.runnect.presentation.mypage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyPageViewModel : ViewModel() {
    val nickName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
}