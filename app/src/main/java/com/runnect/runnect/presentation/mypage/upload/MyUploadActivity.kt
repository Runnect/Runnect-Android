package com.runnect.runnect.presentation.mypage.upload

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityMyUploadBinding
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen
import com.runnect.runnect.presentation.discover.DiscoverFragment
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import com.runnect.runnect.presentation.discover.pick.DiscoverPickActivity
import com.runnect.runnect.presentation.mypage.upload.adapter.MyUploadAdapter
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_COURSE_UPLOAD_IN_UPLOADED_COURSE
import com.runnect.runnect.util.callback.listener.OnMyUploadItemClick
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration
import com.runnect.runnect.util.extension.getCompatibleParcelableExtra
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.setCustomDialog
import com.runnect.runnect.util.extension.setDialogButtonClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_delete.btn_delete_no
import kotlinx.android.synthetic.main.custom_dialog_delete.btn_delete_yes
import timber.log.Timber

@AndroidEntryPoint
class MyUploadActivity : BindingActivity<ActivityMyUploadBinding>(R.layout.activity_my_upload),
    OnMyUploadItemClick {
    private val viewModel: MyUploadViewModel by viewModels()
    private lateinit var uploadAdapter: MyUploadAdapter
    private lateinit var dialog: AlertDialog

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedCourse: EditableDiscoverCourse =
                    result.data?.getCompatibleParcelableExtra(DiscoverFragment.EXTRA_EDITABLE_DISCOVER_COURSE)
                        ?: return@registerForActivityResult

                if (updatedCourse.isDeleted) {
                    viewModel.getUserUploadCourse()
                } else {
                    uploadAdapter.updateMyUploadItem(
                        publicCourseId = viewModel.clickedCourseId,
                        updatedCourse = updatedCourse
                    )
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        viewModel.getUserUploadCourse()
        initLayout()
        addListener()
        addObserver()
        initDialog()
        pullToRefresh()
        registerBackPressedCallback()
    }

    private fun initLayout() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvMyPageUpload.layoutManager = GridLayoutManager(this, 2)
        binding.rvMyPageUpload.addItemDecoration(
            GridSpacingItemDecoration(
                context = this,
                spanCount = 2,
                horizontalSpaceSize = 6,
                topSpaceSize = 20
            )
        )
    }

    private fun addListener() {
        binding.ivMyPageUploadBack.setOnClickListener {
            navigateToPreviousScreenWithAnimation()
        }
        binding.btnMyPageUploadEditCourse.setOnClickListener {
            handleEditClicked()
        }
        binding.tvMyPageUploadDelete.setOnClickListener {
            handleDeleteButtonClicked(it)
        }
        binding.cvUploadMyPageUploadCourse.setOnClickListener {
            Analytics.logClickedItemEvent(EVENT_CLICK_COURSE_UPLOAD_IN_UPLOADED_COURSE)
            startActivity(Intent(this, DiscoverPickActivity::class.java))
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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

    private fun pullToRefresh() {
        binding.refreshLayout.setOnRefreshListener {
            viewModel.getUserUploadCourse()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun handleEditClicked() {
        viewModel.convertMode()
        binding.tvMyPageUploadDelete.isVisible = viewModel.editMode.value!!
    }

    private fun handleDeleteButtonClicked(it: View) {
        if (it.isActivated) {
            setDialogClickEvent()
            dialog.show()
        }
    }

    private fun initDialog() {
        dialog = setCustomDialog(
            layoutInflater, binding.root,
            DESCRIPTION_DIALOG,
            DELETE_BTN
        )
    }

    private fun setDialogClickEvent() {
        dialog.setDialogButtonClickListener { which ->
            when (which) {
                dialog.btn_delete_yes -> {
                    viewModel.deleteUploadCourse()
                    dialog.dismiss()
                }

                dialog.btn_delete_no -> {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun initAdapter() {
        uploadAdapter = MyUploadAdapter(this).apply {
            submitList(
                viewModel.myUploadCourses
            )
        }
        binding.rvMyPageUpload.adapter = uploadAdapter
    }

    private fun addObserver() {
        viewModel.myUploadCourseState.observe(this) {
            when (it) {
                UiState.Empty -> handleEmptyUploadCourse()
                UiState.Loading -> handleLoadingUploadCourse()
                UiState.Success -> handleSuccessfulCourseLoad()
                UiState.Failure -> handleUnsuccessfulUploadCall()
            }
        }

        viewModel.myUploadDeleteState.observe(this) {
            updateDeleteButton(viewModel.selectedItemsCount.value ?: 0)
            when (it) {
                UiState.Empty -> handleEmptyUploadCourse()
                UiState.Loading -> handleLoadingUploadCourse()
                UiState.Success -> handleSuccessfulUploadDeletion()
                UiState.Failure -> handleUnsuccessfulUploadCall()
            }
        }

        viewModel.editMode.observe(this) { editMode ->
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
            tvMyPageUploadTotalCourseCount.text = DESCRIPTION_CHOICE_MODE
            if (::uploadAdapter.isInitialized) uploadAdapter.handleCheckBoxVisibility(true)
        }
    }

    /**편집 취소 버튼을 누르는 경우 or 모든 기록이 삭제된 경우*/
    private fun exitEditMode() {
        with(binding) {
            btnMyPageUploadEditCourse.text = EDIT_MODE
            tvMyPageUploadTotalCourseCount.text = viewModel.getCourseCount()
            tvMyPageUploadDelete.isVisible = viewModel.editMode.value!!
            if (::uploadAdapter.isInitialized) {
                uploadAdapter.clearSelection()
                uploadAdapter.handleCheckBoxVisibility(false)
            }
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

    private fun handleEmptyUploadCourse() {
        with(binding) {
            indeterminateBar.isVisible = false
            constMyPageUploadEditBar.isVisible = false
            svMyPageUpload.isVisible = false
            layoutMyPageUploadNoResult.isVisible = true
        }
    }

    private fun handleLoadingUploadCourse() {
        with(binding) {
            indeterminateBar.isVisible = true
            constMyPageUploadEditBar.isVisible = false
            svMyPageUpload.isVisible = false
        }
    }

    private fun handleSuccessfulCourseLoad() {
        with(binding) {
            indeterminateBar.isVisible = false
            layoutMyPageUploadNoResult.isVisible = false

            tvMyPageUploadTotalCourseCount.text = viewModel.getCourseCount()
            constMyPageUploadEditBar.isVisible = true
            svMyPageUpload.isVisible = true
        }

        initAdapter()
    }

    private fun handleSuccessfulUploadDeletion() {
        binding.indeterminateBar.isVisible = false
        uploadAdapter.removeItems(viewModel.itemsToDelete)
        uploadAdapter.clearSelection()
        viewModel.clearItemsToDelete()
    }

    private fun handleUnsuccessfulUploadCall() {
        binding.indeterminateBar.isVisible = false
        Timber.tag(ContentValues.TAG).d("Failure : ${viewModel.errorMessage.value}")
    }

    override fun selectItem(id: Int): Boolean {
        return if (viewModel.editMode.value == true) {
            Timber.tag(ContentValues.TAG).d("코스 아이디 : $id")
            viewModel.modifyItemsToDelete(id)
            true
        } else {
            viewModel.saveClickedCourseId(id)
            Intent(this, CourseDetailActivity::class.java).apply {
                putExtra(EXTRA_PUBLIC_COURSE_ID, id)
                putExtra(EXTRA_ROOT_SCREEN, CourseDetailRootScreen.MY_PAGE_UPLOAD_COURSE)
                resultLauncher.launch(this)
            }
            false
        }
    }

    companion object {
        const val DESCRIPTION_CHOICE_MODE = "기록 선택"
        const val EDIT_CANCEL = "취소"
        const val EDIT_MODE = "선택"
        const val DESCRIPTION_DIALOG = "코스를 정말로 삭제하시겠어요?"
        const val DELETE_BTN = "삭제하기"
        const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        const val EXTRA_ROOT_SCREEN = "rootScreen"
    }
}