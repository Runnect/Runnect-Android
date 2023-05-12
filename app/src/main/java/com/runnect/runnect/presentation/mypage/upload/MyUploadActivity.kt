package com.runnect.runnect.presentation.mypage.upload

import android.content.ContentValues
import android.os.Bundle
import android.view.View
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
import com.runnect.runnect.util.callback.OnUploadItemClick
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyUploadActivity : BindingActivity<ActivityMyUploadBinding>(R.layout.activity_my_upload), OnUploadItemClick {
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
        binding.tvMyPageUploadDelete.setOnClickListener {
            handleDeleteButtonClicked(it)
        }
    }
    private fun handleEditClicked(){
        viewModel.convertMode()
        binding.tvMyPageUploadDelete.isVisible = viewModel.editMode.value!!
    }
    private fun handleDeleteButtonClicked(it: View){
        if (it.isActivated) {
            showToast("Show Dialog")
        }
    }

    private fun initAdapter() {
        adapter = MyUploadAdapter(this,this).apply {
            submitList(
                viewModel.myUploadCourses
            )
        }
        binding.rvMyPageUpload.adapter = adapter
    }

    private fun addObserver() {

        viewModel.myUploadCourseState.observe(this) {
            when (it) {
                UiState.Empty -> {
                    handleEmptyUploadCourse()
                }
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

        viewModel.selectedItemsCount.observe(this) { count ->
            updateDeleteButton(count)
        }

        viewModel.selectCountMediator.observe(this) {}
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
            tvMyPageUploadDelete.isVisible = viewModel.editMode.value!!
            if (::adapter.isInitialized) adapter.clearSelection()
            viewModel.clearItemsToDelete()
        }
    }

    private fun updateDeleteButton(count: Int) {
        with(binding.tvMyPageUploadDelete) {
            isActivated = count != 0
            text = updateDeleteButtonLabel(count)
        }
    }

    private fun updateDeleteButtonLabel(count: Int): String {
        return if (count == 0) {
            DELETE_BTN
        } else {
            "${DELETE_BTN}(${count})"
        }
    }

    private fun handleSuccessfulCourseLoad(){
        with(binding){
            indeterminateBar.isVisible = false
            tvMyPageUploadTotalCourseCount.text = viewModel.getCourseCount()
            constMyPageUploadEditBar.isVisible = true
            layoutMyPageUploadNoResult.isVisible = false
        }
        initAdapter()
    }
    private fun handleEmptyUploadCourse(){
        with(binding) {
            indeterminateBar.isVisible = false
            constMyPageUploadEditBar.isVisible = false
            layoutMyPageUploadNoResult.isVisible = true
        }
    }
    override fun selectItem(id: Int): Boolean {
        return if (viewModel.editMode.value == true) {
            viewModel.modifyItemsToDelete(id)
            true
        } else {
            //매개변수로 아이템 정보를 넘겨받아 코스 상세 액티비티 이동
            showToast("코스 상세 조회 액티비티 이동")
            false
        }
    }
    companion object {
        const val CHOICE_MODE_DESC = "기록 선택"
        const val EDIT_CANCEL = "취소"
        const val EDIT_MODE = "편집"
        const val DELETE_BTN = "삭제하기"
    }
}