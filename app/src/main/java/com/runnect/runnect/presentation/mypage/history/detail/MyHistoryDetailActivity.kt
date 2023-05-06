package com.runnect.runnect.presentation.mypage.history.detail

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityMyHistoryDetailBinding

class MyHistoryDetailActivity :
    BindingActivity<ActivityMyHistoryDetailBinding>(R.layout.activity_my_history_detail) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addListener()
    }
    private fun addListener(){
        binding.ivShowMore.setOnClickListener {
            updateConstraintForEditMode()
        }
        binding.ivBackBtn.setOnClickListener {
            updateConstraintForReadMode()
        }

    }
    private fun updateConstraintForEditMode() {
        val constraintLayout = binding.constMyHistoryDetail
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        with(constraintSet) {
            this.connect(
                binding.dividerCourseTitle.id,
                ConstraintSet.TOP,
                binding.ivDetailCourseImageEdit.id,
                ConstraintSet.BOTTOM
            )
            applyTo(constraintLayout)
        }
        with(binding){
            ivDetailCourseImage.isVisible = false
            ivDetailCourseImageEdit.isVisible = true
            tvHistoryEditFinish.isVisible = true
        }
    }
    private fun updateConstraintForReadMode() {
        val constraintLayout = binding.constMyHistoryDetail
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        with(constraintSet) {
            this.connect(
                binding.dividerCourseTitle.id,
                ConstraintSet.TOP,
                binding.ivDetailCourseImage.id,
                ConstraintSet.BOTTOM
            )
            applyTo(constraintLayout)
        }
        with(binding){
            ivDetailCourseImage.isVisible = true
            ivDetailCourseImageEdit.isVisible = false
            tvHistoryEditFinish.isVisible = false
        }
    }
}