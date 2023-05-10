package com.runnect.runnect.presentation.mypage.upload

import android.content.ContentValues
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityMyUploadBinding
import com.runnect.runnect.presentation.mypage.history.MyHistoryActivity
import com.runnect.runnect.presentation.mypage.upload.adapter.MyUploadAdapter
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyUploadActivity : BindingActivity<ActivityMyUploadBinding>(R.layout.activity_my_upload) {
    private val viewModel: MyUploadViewModel by viewModels()

    private lateinit var adapter: MyUploadAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        viewModel.getUserUploadCourse()
        initLayout()
        addListener()
        addObserver()
    }

    private fun initLayout() {
        binding.rvMyPageUpload.layoutManager = GridLayoutManager(this, 2)
        binding.rvMyPageUpload.addItemDecoration(GridSpacingItemDecoration(this, 2, 6, 18))
    }

    private fun addListener() {
        binding.ivMyPageUploadBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        binding.btnMyPageUploadEditCourse.setOnClickListener {
            handleEditClicked()
        }
    }
    private fun handleEditClicked(){
        viewModel.convertMode()
    }

    private fun initAdapter() {
        adapter = MyUploadAdapter(this).apply {
            submitList(
                viewModel.myUploadCourses
            )
        }
        binding.rvMyPageUpload.adapter = adapter
    }

    private fun addObserver() {

        viewModel.myUploadCourseState.observe(this) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    handleSuccessfulCourseLoad()
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.errorMessage.value}")
                }
            }
        }
        viewModel.editMode.observe(this){editMode ->
            if (editMode) {
                enterEditMode()
            } else {
                exitEditMode()
            }
        }
    }
    private fun enterEditMode() {
        with(binding) {
            btnMyPageUploadEditCourse.text = EDIT_CANCEL
            tvMyPageUploadTotalCourseCount.text = CHOICE_MODE_DESC
        }
    }
    /**편집 취소 버튼을 누르는 경우 or 모든 기록이 삭제된 경우*/
    private fun exitEditMode() {
        with(binding) {
            btnMyPageUploadEditCourse.text = EDIT_MODE
            tvMyPageUploadTotalCourseCount.text = viewModel.getCourseCount()
        }
    }

    private fun handleSuccessfulCourseLoad(){
        with(binding){
            indeterminateBar.isVisible = false
            tvMyPageUploadTotalCourseCount.text = viewModel.getCourseCount()
            constMyPageUploadEditBar.isVisible = true
            //NoResultView visible false
        }
        initAdapter()
    }
    companion object {
        const val CHOICE_MODE_DESC = "기록 선택"
        const val EDIT_CANCEL = "취소"
        const val EDIT_MODE = "편집"
    }
}