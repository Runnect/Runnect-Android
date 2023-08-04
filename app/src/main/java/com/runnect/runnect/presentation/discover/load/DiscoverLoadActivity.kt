package com.runnect.runnect.presentation.discover.load

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityDiscoverLoadSelectBinding
import com.runnect.runnect.presentation.discover.load.adapter.DiscoverLoadAdapter
import com.runnect.runnect.presentation.discover.upload.DiscoverUploadActivity
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.OnRecommendCourseClick
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DiscoverLoadActivity :
    BindingActivity<ActivityDiscoverLoadSelectBinding>(R.layout.activity_discover_load_select),
    OnRecommendCourseClick {
    private val viewModel: DiscoverLoadViewModel by viewModels()
    private lateinit var adapter: DiscoverLoadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        initLayout()
        viewModel.getMyCourseLoad()
        addObserver()
        addListener()
    }

    private fun initLayout() {
        binding.rvDiscoverLoadSelect.apply {
            layoutManager = GridLayoutManager(this@DiscoverLoadActivity, 2)
            addItemDecoration(
                GridSpacingItemDecoration(
                    this@DiscoverLoadActivity, 2, 6, 18
                )
            )
        }
    }

    private fun addObserver() {
        viewModel.idSelectedItem.observe(this) {
            Timber.d("4. ViewModel에서 변경된 라이브데이터 관찰")
            binding.ivDiscoverLoadSelectFinish.isActivated = it != 0
        }

        viewModel.courseLoadState.observe(this) {
            when (it) {
                UiState.Empty -> {
                    handleEmptyCourseLoad()
                }

                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    handleSuccessfulCourseLoad()
                }

                UiState.Failure -> {
                    handleUnsuccessfulCourseLoad()
                }
            }
        }
    }

    private fun handleEmptyCourseLoad() {
        with(binding) {
            indeterminateBar.isVisible = false
            layoutDiscoverLoadSelect.isVisible = true
            tvDiscoverLoadSelectFinish.isVisible = false
            ivDiscoverLoadSelectFinish.isVisible = false
        }
    }

    private fun handleSuccessfulCourseLoad() {
        initAdapter()
        with(binding) {
            indeterminateBar.isVisible = false
            layoutDiscoverLoadSelect.isVisible = false
            tvDiscoverLoadSelectFinish.isVisible = true
            ivDiscoverLoadSelectFinish.isVisible = true
        }
    }

    private fun handleUnsuccessfulCourseLoad() {
        binding.indeterminateBar.isVisible = false
        Timber.tag(ContentValues.TAG)
            .d("Failure : ${viewModel.errorMessage.value}")
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun addListener() {
        binding.ivDiscoverLoadSelectBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        binding.ivDiscoverLoadSelectFinish.setOnClickListener {
            if (it.isActivated) {
                val intent = Intent(this, DiscoverUploadActivity::class.java)
                intent.apply {
                    putExtra(EXTRA_COURSE_ID, viewModel.idSelectedItem.value)
                    putExtra(EXTRA_IMG, viewModel.imgSelectedItem.value)
                    putExtra(EXTRA_DEPARTURE, viewModel.departureSelectedItem.value)
                    putExtra(EXTRA_DISTANCE, viewModel.distanceSelectedItem.value)
                }
                startActivity(intent)
                adapter.clearSelection()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
        binding.cvDiscoverDrawCourse.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }

    private fun initAdapter() {
        adapter = DiscoverLoadAdapter(this, this).apply {
            submitList(
                viewModel.courseLoadList
            )
        }
        binding.rvDiscoverLoadSelect.adapter = this@DiscoverLoadActivity.adapter
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
