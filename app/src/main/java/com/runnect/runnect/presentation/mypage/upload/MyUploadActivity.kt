package com.runnect.runnect.presentation.mypage.upload

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityMyUploadBinding
import com.runnect.runnect.presentation.mypage.upload.adapter.MyUploadAdapter
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyUploadActivity : BindingActivity<ActivityMyUploadBinding>(R.layout.activity_my_upload) {
    private val viewModel: MyUploadViewModel by viewModels()

    private lateinit var adapter: MyUploadAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        viewModel.getUserUploadCourse()
        initLayout()
        addListener()
        addObserver()
    }

    private fun initLayout() {
        binding.rvMyPageUpload.layoutManager = GridLayoutManager(this, 2)
        binding.rvMyPageUpload.addItemDecoration(GridSpacingItemDecoration(this, 2, 6, 18))
    }

    private fun addListener() {
        binding.ivMyPageUploadBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }
    private fun initAdapter() {
        adapter = MyUploadAdapter(this).apply {
            submitList(
                viewModel.myUploadCourseList
            )
        }
        binding.rvMyPageUpload.adapter = adapter
    }

    private fun addObserver() {
        viewModel.myUploadCourseState.observe(this) { state ->
            if (state == UiState.Success) {
                initAdapter()
            }
        }
    }
}