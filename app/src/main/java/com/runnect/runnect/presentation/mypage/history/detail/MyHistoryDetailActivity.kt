package com.runnect.runnect.presentation.mypage.history.detail

import android.os.Bundle
import android.text.InputType
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import coil.load
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.databinding.ActivityMyHistoryDetailBinding
import com.runnect.runnect.util.extension.customGetSerializable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyHistoryDetailActivity :
    BindingActivity<ActivityMyHistoryDetailBinding>(R.layout.activity_my_history_detail) {
    private lateinit var historyData: HistoryInfoDTO
    private val viewModel: MyHistoryDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        addListener()
    }

    private fun initLayout() {
        val bundle = intent.getBundleExtra("historyData")
        historyData = bundle?.customGetSerializable<HistoryInfoDTO>("historyDataBundle")!!
        with(binding) {
            vm = viewModel
            lifecycleOwner = this@MyHistoryDetailActivity
            ivDetailCourseImage.load(historyData.img)
            enterReadMode()
        }
        setHistoryData()
    }

    private fun setHistoryData() {
        with(historyData) {
            viewModel.setTitle(title)
            viewModel.date.value = date
            viewModel.departure.value = location
            viewModel.distance.value = distance
            viewModel.time.value = time
            viewModel.pace.value = pace
        }
    }

    private fun addListener() {
        binding.ivShowMore.setOnClickListener {
            updateConstraintForEditMode()
            enterEditMode()
        }
        binding.ivBackBtn.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
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
        with(binding) {
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
        with(binding) {
            ivDetailCourseImage.isVisible = true
            ivDetailCourseImageEdit.isVisible = false
            tvHistoryEditFinish.isVisible = false
        }
    }

    private fun enterEditMode() {
        enableEditTitle()
    }

    private fun enterReadMode() {
        disableEditTitle()
    }

    private fun enableEditTitle() {
        binding.etCourseTitle.inputType = InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
    }

    private fun disableEditTitle() {
        binding.etCourseTitle.inputType = InputType.TYPE_NULL
    }
}