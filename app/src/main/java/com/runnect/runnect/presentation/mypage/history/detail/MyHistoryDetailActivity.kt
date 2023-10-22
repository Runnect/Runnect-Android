package com.runnect.runnect.presentation.mypage.history.detail

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.databinding.ActivityMyHistoryDetailBinding
import com.runnect.runnect.presentation.mypage.history.MyHistoryActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.custom.CommonDialogFragment
import com.runnect.runnect.util.custom.PopupItem
import com.runnect.runnect.util.custom.RunnectPopupMenu
import com.runnect.runnect.util.extension.getCompatibleSerializableExtra
import com.runnect.runnect.util.extension.showToast
import com.runnect.runnect.util.extension.snackBar
import com.runnect.runnect.util.extension.stringOf
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyHistoryDetailActivity :
    BindingActivity<ActivityMyHistoryDetailBinding>(R.layout.activity_my_history_detail) {
    private val viewModel: MyHistoryDetailViewModel by viewModels()
    private var currentScreenMode: ScreenMode = ScreenMode.ReadOnlyMode
    private var currentTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this@MyHistoryDetailActivity
        binding.vm = viewModel

        initLayout()
        addListener()
        addObserver()
        registerBackPressedCallback()
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //handleIsEdited(viewModel.titleForInterruption.isEmpty())

                when (currentScreenMode) {
                    is ScreenMode.ReadOnlyMode -> navigateToPreviousScreen()
                    is ScreenMode.EditMode -> showStopEditingDialog()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun initLayout() {
        val bundle = intent.getBundleExtra(HISTORY_INTENT_KEY)
        val runningHistory: HistoryInfoDTO? =
            bundle?.getCompatibleSerializableExtra(HISTORY_BUNDLE_KEY)
        initRunningHistory(runningHistory)
        enterReadMode()
    }

    private fun initRunningHistory(dto: HistoryInfoDTO?) {
        if (dto != null) {
            binding.dto = dto
            viewModel.apply {
                setInitialHistoryTitle(dto.title)
                setCurrentHistoryId(dto.id)
            }
        }
    }

    private fun enterReadMode() {
        currentScreenMode = ScreenMode.ReadOnlyMode
        binding.ivShowMore.isVisible = true
        disableTitleEditing()
        updateConstraintForReadMode()
    }

    private fun addListener() {
        binding.ivBackBtn.setOnClickListener {
//            if (currentScreenMode == EDIT_MODE) {
////                editInterruptDialog.show()
//            } else {
//                handleIsEdited(viewModel.titleForInterruption.isEmpty())
//            }

            when (currentScreenMode) {
                is ScreenMode.ReadOnlyMode -> navigateToPreviousScreen()
                is ScreenMode.EditMode -> showStopEditingDialog()
            }
        }

        binding.ivShowMore.setOnClickListener { view ->
            showPopupMenu(view)
        }

        binding.tvHistoryEditFinish.setOnClickListener {
            viewModel.patchHistoryTitle()
        }
    }

    private fun showPopupMenu(anchorView: View) {
        val popupItems = listOf(
            PopupItem(R.drawable.ic_detail_more_edit, getString(R.string.popup_menu_item_edit)),
            PopupItem(R.drawable.ic_detail_more_delete, getString(R.string.popup_menu_item_delete))
        )

        RunnectPopupMenu(anchorView.context, popupItems) { _, _, pos ->
            when (pos) {
                0 -> {
                    // todo: 여기서 팝업메뉴를 dismiss 하는 코드는 안 필요한가?
                    enterEditMode()
                }

                1 -> showHistoryDeleteDialog()
            }
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun RunnectPopupMenu.showCustomPosition(anchorView: View) {
        showAsDropDown(anchorView, POPUP_MENU_X_OFFSET, POPUP_MENU_Y_OFFSET, Gravity.END)
    }

    private fun enterEditMode() {
        currentScreenMode = ScreenMode.EditMode
        binding.ivShowMore.isVisible = false

        saveCurrentTitle()
//        viewModel.titleForInterruption = viewModel._title.value.toString()
        updateConstraintForEditMode()
        enableTitleEditing()
    }

    private fun saveCurrentTitle() {
        currentTitle = viewModel.title
        Timber.e("현재 제목: $currentTitle")
    }

    private fun handleIsEdited(isEdited: Boolean) {
        if (isEdited) {
            navigateToPreviousScreen()
        } else {
            val intent = Intent(this, MyHistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            navigateToPreviousScreen()
        }
    }

    private fun navigateToPreviousScreen() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun addObserver() {
        setupTitleObserver()
        setupHistoryDeleteStateObserver()
        setupTitleEditStateObserver()
    }

    private fun setupTitleObserver() {
        viewModel._title.observe(this) { title ->
            with(binding.tvHistoryEditFinish) {
                if (title.isNullOrEmpty()) {
                    isActivated = false
                    isClickable = false
                } else {
                    isActivated = true
                    isClickable = true
                }
            }
        }
    }

    private fun setupHistoryDeleteStateObserver() {
        viewModel.historyDeleteState.observe(this) { state ->
            when (state) {
                is UiStateV2.Loading -> showLoadingProgressBar()

                is UiStateV2.Success -> {
                    dismissLoadingProgressBar()
                    navigateToMyHistoryScreen()
                }

                is UiStateV2.Failure -> {
                    dismissLoadingProgressBar()
                    snackBar(binding.root, state.msg)
                }

                else -> {}
            }
        }
    }

    private fun navigateToMyHistoryScreen() {
        Intent(this, MyHistoryActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(this)
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun setupTitleEditStateObserver() {
        viewModel.titlePatchState.observe(this) { state ->
            when (state) {
                is UiStateV2.Loading -> showLoadingProgressBar()

                is UiStateV2.Success -> {
                    dismissLoadingProgressBar()
                    enterReadMode()

                    val response = state.data ?: return@observe
                    updateCurrentTitle(response.record.title)
//                    viewModel.titleForInterruption = viewModel._title.value.toString()

                    showToast("수정이 완료되었습니다")
                }

                is UiStateV2.Failure -> {
                    dismissLoadingProgressBar()
                }

                else -> {}
            }
        }
    }

    private fun updateCurrentTitle(title: String) {
        currentTitle = title
    }

    private fun showLoadingProgressBar() {
        binding.pbMyHistoryDetailLoading.isVisible = true
    }

    private fun dismissLoadingProgressBar() {
        binding.pbMyHistoryDetailLoading.isVisible = false
    }

    private fun updateConstraintForReadMode() {
        val constraintLayout = binding.constMyHistoryDetail
        val constraintSet = ConstraintSet()
        constraintSet.apply {
            clone(constraintLayout)
            connect(
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

    private fun updateConstraintForEditMode() {
        val constraintLayout = binding.constMyHistoryDetail
        val constraintSet = ConstraintSet()
        constraintSet.apply {
            clone(constraintLayout)
            connect(
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

    private fun disableTitleEditing() {
        binding.etCourseTitle.inputType = InputType.TYPE_NULL
    }

    private fun enableTitleEditing() {
        binding.etCourseTitle.inputType = InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE

        // TODO: 키보드가 올라오도록
    }

//    private fun initEditBottomSheet() {
//        detailMoreBottomSheet = setEditBottomSheet()
//        setEditBottomSheetClickEvent()
//    }
//
//    private fun setEditBottomSheetClickEvent() {
//        detailMoreBottomSheet.setEditBottomSheetClickListener { which ->
//            when (which) {
//                detailMoreBottomSheet.layout_edit_frame -> {
//                    enterEditMode()
//                }
//
//                detailMoreBottomSheet.layout_delete_frame -> {
//                    detailMoreBottomSheet.dismiss()
//                    deleteDialog.show()
//                }
//            }
//        }
//    }

    private fun showHistoryDeleteDialog() {
        val dialog = CommonDialogFragment(
            stringOf(R.string.dialog_my_history_detail_delete_desc),
            stringOf(R.string.dialog_my_history_detail_delete_no),
            stringOf(R.string.dialog_my_history_detail_delete_yes),
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = { viewModel.deleteHistory() }
        )
        dialog.show(supportFragmentManager, TAG_MY_HISTORY_DELETE_DIALOG)
    }

    private fun showStopEditingDialog() {
        val dialog = CommonDialogFragment(
            stringOf(R.string.dialog_my_history_detail_stop_editing_desc),
            stringOf(R.string.dialog_my_history_detail_stop_editing_no),
            stringOf(R.string.dialog_my_history_detail_stop_editing_yes),
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = {
                enterReadMode()

                // TODO: 원래 제목으로 다시 복구한다.
                viewModel.restoreInitialTitle(currentTitle)
//                viewModel._title.value = viewModel.titleForInterruption
            }
        )
        dialog.show(supportFragmentManager, TAG_MY_HISTORY_EDIT_DIALOG)
    }

    companion object {
        private const val HISTORY_BUNDLE_KEY = "historyDataBundle"
        private const val HISTORY_INTENT_KEY = "historyData"
        private const val EDIT_MODE = "editMode"
        private const val READ_MODE = "readMode"
        private const val POPUP_MENU_X_OFFSET = 17
        private const val POPUP_MENU_Y_OFFSET = -10
        private const val TAG_MY_HISTORY_DELETE_DIALOG = "MY_HISTORY_DELETE_DIALOG"
        private const val TAG_MY_HISTORY_EDIT_DIALOG = "MY_HISTORY_EDIT_DIALOG"
    }
}