package com.example.runnect.presentation.mypage

import android.os.Bundle
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.databinding.ActivityMyUploadBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyUploadActivity : BindingActivity<ActivityMyUploadBinding>(R.layout.activity_my_upload) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}