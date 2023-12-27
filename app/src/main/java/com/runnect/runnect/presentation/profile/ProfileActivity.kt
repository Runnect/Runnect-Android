package com.runnect.runnect.presentation.profile

import android.os.Bundle
import androidx.activity.viewModels
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : BindingActivity<ActivityProfileBinding>(R.layout.activity_profile) {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var adapter: ProfileCourseAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        initAdapter()
    }

    private fun initAdapter() {
        adapter = ProfileCourseAdapter().also { adapter ->
            binding.rvProfileUploadCourse.adapter = adapter
            adapter.submitList(viewModel.courseList)
        }
    }
}