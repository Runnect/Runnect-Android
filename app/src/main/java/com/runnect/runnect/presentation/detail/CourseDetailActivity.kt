package com.runnect.runnect.presentation.detail

import android.os.Bundle
import androidx.activity.viewModels
import coil.load
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityCourseDetailBinding
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CourseDetailActivity :
    BindingActivity<ActivityCourseDetailBinding>(R.layout.activity_course_detail) {
    private val viewModel: CourseDetailViewModel by viewModels()
    private var courseId: Int =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseId = intent.getIntExtra("courseId", 0)//선택한 코스의 id로 API 호출 예정
        addListener()
        initView()
        addObserver()
        viewModel.getCourseDetail(courseId)
    }

    private fun initView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    private fun addListener() {
        binding.ivCourseDetailBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
        binding.ivCourseDetailScrap.setOnClickListener {
            it.isSelected = !it.isSelected
            viewModel.postCourseScrap(id = courseId, it.isSelected)
        }
        binding.ivCourseDetailFinish.setOnClickListener {
            //러닝 시작
        }
    }

    private fun addObserver() {
        viewModel.courseDetailState.observe(this) { state ->
            Timber.d("Activity호출 상세코스 UiState $state")
            if (state == UiState.Success) {
                with(binding) {
                    with(viewModel.courseDetail) {
                        ivCourseDetailMap.load(image)
                        ivCourseDetailProfileStamp.load(stampId)
                        ivCourseDetailProfileNickname.text = nickname
                        tvCourseDetailProfileLv.text = level
                        tvCourseDetailTitle.text = title
                        tvCourseDetailDistance.text = distance
                        tvCourseDetailDeparture.text = departure
                        tvCourseDetailDesc.text = description
                        ivCourseDetailScrap.isSelected = scrap
                    }

                }
            }
        }
    }
}