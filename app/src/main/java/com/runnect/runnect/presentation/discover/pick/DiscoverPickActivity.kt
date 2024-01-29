package com.runnect.runnect.presentation.discover.pick

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityDiscoverPickBinding
import com.runnect.runnect.domain.entity.DiscoverUploadCourse
import com.runnect.runnect.presentation.discover.pick.adapter.DiscoverPickAdapter
import com.runnect.runnect.presentation.discover.upload.DiscoverUploadActivity
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.callback.listener.OnCourseUploadItemClick
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DiscoverPickActivity :
    BindingActivity<ActivityDiscoverPickBinding>(R.layout.activity_discover_pick),
    OnCourseUploadItemClick {
    private val viewModel: DiscoverPickViewModel by viewModels()
    private lateinit var discoverPickAdapter: DiscoverPickAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        initLayout()
        addListener()
        addObserver()
        registerBackPressedCallback()
    }

    private fun initLayout() {
        binding.rvDiscoverPick.apply {
            layoutManager = GridLayoutManager(this@DiscoverPickActivity, 2)
            addItemDecoration(
                GridSpacingItemDecoration(
                    context = this@DiscoverPickActivity,
                    spanCount = 2,
                    horizontalSpaceSize = 6,
                    topSpaceSize = 20
                )
            )
        }
    }

    private fun addListener() {
        initBackButtonClickListener()
        initPickFinishButtonClickListener()
        initGoToDrawCourseClickListener()
    }

    private fun initBackButtonClickListener() {
        binding.ivDiscoverPickBack.setOnClickListener {
            navigateToPreviousScreenWithAnimation()
        }
    }

    private fun initPickFinishButtonClickListener() {
        binding.ivDiscoverPickFinish.setOnClickListener {
            if (it.isActivated) {
                val intent = Intent(this, DiscoverUploadActivity::class.java)
                intent.apply {
                    putExtra(EXTRA_COURSE_ID, viewModel.idSelectedItem.value)
                    putExtra(EXTRA_IMG, viewModel.imgSelectedItem.value)
                    putExtra(EXTRA_DEPARTURE, viewModel.departureSelectedItem.value)
                    putExtra(EXTRA_DISTANCE, viewModel.distanceSelectedItem.value)
                }
                startActivity(intent)
                discoverPickAdapter.clearSelection()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    private fun initGoToDrawCourseClickListener() {
        binding.cvDiscoverDrawCourse.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }

    private fun addObserver() {
        setupItemSelectObserver()
        setupUploadCourseGetStateObserver()
    }

    private fun setupItemSelectObserver() {
        viewModel.idSelectedItem.observe(this) {
            binding.ivDiscoverPickFinish.isActivated = it != 0
        }
    }

    private fun setupUploadCourseGetStateObserver() {
        viewModel.uploadCourseGetState.observe(this) { state ->
            when (state) {
                is UiStateV2.Empty -> handleEmptyCourseLoad()
                is UiStateV2.Loading -> {
                    binding.indeterminateBar.isVisible = true
                }

                is UiStateV2.Success -> {
                    val courses = state.data ?: return@observe
                    handleSuccessfulCourseLoad(courses)
                }

                is UiStateV2.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    showSnackbar(anchorView = binding.root, message = state.msg)
                }
            }
        }
    }

    private fun handleEmptyCourseLoad() {
        with(binding) {
            indeterminateBar.isVisible = false
            layoutDiscoverPick.isVisible = true
            tvDiscoverPickFinish.isVisible = false
            ivDiscoverPickFinish.isVisible = false
        }
    }

    private fun handleSuccessfulCourseLoad(courses: List<DiscoverUploadCourse>) {
        initAdapter(courses)
        with(binding) {
            indeterminateBar.isVisible = false
            layoutDiscoverPick.isVisible = false
            tvDiscoverPickFinish.isVisible = true
            ivDiscoverPickFinish.isVisible = true
        }
    }

    private fun initAdapter(courses: List<DiscoverUploadCourse>) {
        discoverPickAdapter = DiscoverPickAdapter(
            onCourseUploadItemClick = this
        ).apply {
            binding.rvDiscoverPick.adapter = this
            submitList(courses)
        }
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToPreviousScreenWithAnimation()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun selectCourse(id: Int, img: String, departure: String, distance: String) {
        Timber.d("2. Adapter로부터 호출되는 콜백함수 selectItem")
        viewModel.checkSelectEnable(id, img, departure, distance)
    }

    companion object {
        const val EXTRA_COURSE_ID = "courseId"
        const val EXTRA_IMG = "img"
        const val EXTRA_DEPARTURE = "departure"
        const val EXTRA_DISTANCE = "distance"
    }
}
