package com.runnect.runnect.presentation.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.runnect.runnect.navigator.feature.detail.CourseDetailNavigator
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityProfileBinding
import com.runnect.runnect.presentation.detail.navigator.impl.CourseDetailNavigatorImpl
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.VIEW_USER_PROFILE
import com.runnect.runnect.util.extension.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BindingActivity<ActivityProfileBinding>(R.layout.activity_profile) {

    @Inject
    lateinit var detailNavigator: CourseDetailNavigator

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var adapter: ProfileCourseAdapter
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        Analytics.logClickedItemEvent(VIEW_USER_PROFILE)
        initAdapter()
        addListener()
        addObserver()
        getIntentExtra()
        getUserProfile()
    }

    private fun getIntentExtra() {
        userId = intent.getIntExtra(EXTRA_COURSE_USER_ID, -1)
    }

    private fun initAdapter() {
        adapter = ProfileCourseAdapter(onScrapButtonClick = { courseId, scrapTF ->
            viewModel.postCourseScrap(courseId = courseId, scrapTF = scrapTF)
        }, onCourseItemClick = { courseId ->
            navigateToCourseDetail(courseId)
        }).also { adapter ->
            binding.rvProfileUploadCourse.adapter = adapter
        }
    }

    private fun addListener() {
        initBackButtonClickListener()
    }

    private fun addObserver() {
        setupUserProfileGetStateObserver()
        setupCourseScrapPostStateObserver()
    }

    private fun navigateToCourseDetail(courseId: Int) {
        detailNavigator.navigateFrom(
            this,
            intentBuilder = {
                putExtra(CourseDetailNavigator.EXTRA_PUBLIC_COURSE_ID, courseId)
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            },
        )
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

    private fun setupCourseScrapPostStateObserver() {
        viewModel.courseScrapState.observe(this) { state ->
            when (state) {
                is UiStateV2.Loading -> {

                }

                is UiStateV2.Success -> {
                    state.data?.let { it ->
                        adapter.updateCourseItem(
                            courseId = it.publicCourseId.toInt(),
                            scrapTF = it.scrapTF
                        )
                    }
                }

                is UiStateV2.Failure -> {
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
        private const val EXTRA_COURSE_USER_ID = "courseUserId"
    }
}