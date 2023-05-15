package com.runnect.runnect.presentation.mypage.setting

import androidx.lifecycle.ViewModel
import com.runnect.runnect.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MySettingAccountInfoViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
}