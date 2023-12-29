package com.runnect.runnect.presentation.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityProfileBinding
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.extension.showSnackbar
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
        initBackButtonClickListener()
        getIntentExtra()
        getUserProfile()
        setupUserProfileGetStateObserver()
    }

    private fun getIntentExtra() {
        userId = intent.getIntExtra(EXTRA_COURSE_USER_ID, -1)
    }

    private fun initAdapter() {
        adapter = ProfileCourseAdapter(
            onLikeButtonClick = { courseId, scrapTF ->
                viewModel.postCourseScrap(courseId = courseId, scrapTF = scrapTF)
            },
            onCourseItemClick = { courseId ->
                // 코스 디테일로 이동
            }
        ).also { adapter ->
            binding.rvProfileUploadCourse.adapter = adapter
        }
    }


    private fun initBackButtonClickListener() {
        binding.ivProfileBack.setOnClickListener {
            finish()
        }
    }

    private fun getUserProfile() {
        viewModel.getUserProfile(userId = userId)
    }

    private fun setupUserProfileGetStateObserver() {
        viewModel.userProfileState.observe(this) { state ->
            when (state) {
                is UiStateV2.Loading -> {
                    activateLoadingProgressBar()
                }

                is UiStateV2.Success -> {
                    deactivateLoadingProgressBar()
                    binding.data = state.data
                    adapter.submitList(state.data.courseData)
                }

                is UiStateV2.Failure -> {
                    deactivateLoadingProgressBar()
                    this.showSnackbar(binding.root, state.msg)
                }

                else -> {

                }
            }

        }
    }

    private fun activateLoadingProgressBar() {
        binding.clProfile.isVisible = false
        binding.pbProfileIntermediate.isVisible = true
    }

    private fun deactivateLoadingProgressBar() {
        binding.clProfile.isVisible = true
        binding.pbProfileIntermediate.isVisible = false
    }

    companion object {
        private const val EXTRA_COURSE_USER_ID = "userId"
        private const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
    }
}