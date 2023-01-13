package com.example.runnect.presentation.mypage

import android.os.Bundle
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.databinding.ActivityMyRewardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity : BindingActivity<ActivityMyRewardBinding>(R.layout.activity_my_page) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}