package com.runnect.runnect.presentation.detail

import android.os.Bundle
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityCourseDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourseDetailActivity :
    BindingActivity<ActivityCourseDetailBinding>(R.layout.activity_course_detail) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addListener()
        intent.getIntExtra("courseId", 0).toString()//선택한 코스의 id로 API 호출 예정
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    private fun addListener() {
        binding.ivCourseDetailBack.setOnClickListener {
            finish()
        }
        binding.ivCourseDetailScrap.setOnClickListener {
            it.isSelected = !it.isSelected
            //스크랩 여부에 따라 보관함의 코스 생성/삭제
        }
        binding.ivCourseDetailFinish.setOnClickListener {
            //러닝 시작
        }
    }
}