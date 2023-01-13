package com.runnect.runnect.presentation.discover.load

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityDiscoverLoadSelectBinding
import com.runnect.runnect.presentation.discover.load.adapter.DiscoverLoadAdapter
import com.runnect.runnect.presentation.discover.upload.DiscoverUploadActivity
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
        viewModel.courseLoadState.observe(this) { state ->
            if (state == UiState.Success) {
                initAdapter()
            }
        }
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
                    putExtra("courseId", viewModel.idSelectedItem.value)
                    putExtra("img", viewModel.imgSelectedItem.value)
                    putExtra("departure", viewModel.departureSelectedItem.value)
                    putExtra("distance", viewModel.distanceSelectedItem.value)
                }
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
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
}
