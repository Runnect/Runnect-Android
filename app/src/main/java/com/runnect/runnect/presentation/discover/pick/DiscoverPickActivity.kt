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
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration
import com.runnect.runnect.util.extension.applyScreenEnterAnimation
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverPickActivity :
    BindingActivity<ActivityDiscoverPickBinding>(R.layout.activity_discover_pick) {
    private val viewModel: DiscoverPickViewModel by viewModels()
    private lateinit var pickAdapter: DiscoverPickAdapter

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
        initRecyclerView()
    }

    private fun initRecyclerView() {
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
                Intent(this, DiscoverUploadActivity::class.java).apply {
                    putExtra(EXTRA_UPLOAD_COURSE, viewModel.selectedCourse)
                    startActivity(this)
                }
                applyScreenEnterAnimation()
            }
        }
    }

    private fun initGoToDrawCourseClickListener() {
        binding.cvDiscoverDrawCourse.setOnClickListener {
            Intent(this, SearchActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(this)
            }
            finish()
        }
    }

    private fun addObserver() {
        setupItemSelectStateObserver()
        setupUploadCourseGetStateObserver()
    }

    private fun setupItemSelectStateObserver() {
        viewModel.courseSelectState.observe(this) { isSelected ->
            binding.ivDiscoverPickFinish.isActivated = isSelected
        }
    }

    private fun setupUploadCourseGetStateObserver() {
        viewModel.courseGetState.observe(this) { state ->
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
        pickAdapter = DiscoverPickAdapter(
            onCourseItemClick = { isSelected, course ->
                viewModel.apply {
                    updateCourseSelectState(isSelected)
                    if (isSelected) saveSelectedCourse(course)
                }
            }
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

    companion object {
        const val EXTRA_UPLOAD_COURSE = "uploadCourse"
    }
}
