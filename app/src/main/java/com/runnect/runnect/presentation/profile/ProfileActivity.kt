package com.runnect.runnect.presentation.profile

import android.os.Bundle
import androidx.activity.viewModels
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityProfileBinding
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : BindingActivity<ActivityProfileBinding>(R.layout.activity_profile) {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var adapter: ProfileCourseAdapter
    private var userId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        initAdapter()
        getIntentExtra()
        getUserProfile()
        setupUserProfileGetStateObserver()
    }

    private fun getIntentExtra() {
        userId = intent.getIntExtra(EXTRA_COURSE_USER_ID, -1)
    }

    private fun initAdapter() {
        adapter = ProfileCourseAdapter().also { adapter ->
            binding.rvProfileUploadCourse.adapter = adapter
        }
    }


    private fun getUserProfile() {
        viewModel.getUserProfile(userId = userId)
    }

    private fun setupUserProfileGetStateObserver() {
        viewModel.userProfileState.observe(this) { state ->
            when (state) {
                is UiStateV2.Loading -> {
                    // 프로그레스바
                }

                is UiStateV2.Success -> {
                    binding.data = state.data
                    adapter.submitList(state.data.courseData)
                }

                is UiStateV2.Failure -> {

                }

                else -> {

                }
            }

        }
    }

    companion object {
        private const val EXTRA_COURSE_USER_ID = "userId"
    }
}