package com.runnect.runnect.presentation.mypage.upload

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.UploadedCourseDTO
import com.runnect.runnect.databinding.ActivityMyUploadBinding
import com.runnect.runnect.presentation.mypage.upload.adapter.MyUploadAdapter
import com.runnect.runnect.util.GridSpacingItemDecoration

class MyUploadActivity : BindingActivity<ActivityMyUploadBinding>(R.layout.activity_my_upload) {

    private val adapter by lazy {
        MyUploadAdapter(this).apply {
            submitList(
                listOf(
                    UploadedCourseDTO(1, R.drawable.discover_ad_img, "서울시 송파구 한강"),
                    UploadedCourseDTO(2, R.drawable.discover_ad_img, "서울시 송파구 한강"),
                    UploadedCourseDTO(3, R.drawable.discover_ad_img, "서울시 송파구 한강"),
                    UploadedCourseDTO(4, R.drawable.discover_ad_img, "서울시 송파구 한강"),
                    UploadedCourseDTO(5, R.drawable.discover_ad_img, "서울시 송파구 한강"),
                    UploadedCourseDTO(6, R.drawable.discover_ad_img, "서울시 송파구 한강"),
                    UploadedCourseDTO(7, R.drawable.discover_ad_img, "서울시 송파구 한강"),
                    UploadedCourseDTO(8, R.drawable.discover_ad_img, "서울시 송파구 한강"),
                    UploadedCourseDTO(9, R.drawable.discover_ad_img, "서울시 송파구 한강")
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        addListener()
    }

    private fun initLayout() {
        binding.rvMyPageUpload.layoutManager = GridLayoutManager(this, 2)
        binding.rvMyPageUpload.addItemDecoration(GridSpacingItemDecoration(this, 2, 6, 18))
        binding.rvMyPageUpload.adapter = adapter
    }

    private fun addListener() {
        binding.ivMyPageUploadBack.setOnClickListener {
            finish()
        }
    }
}