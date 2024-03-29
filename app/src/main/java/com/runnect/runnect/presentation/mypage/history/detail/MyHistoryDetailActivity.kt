package com.runnect.runnect.presentation.mypage.history.detail

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.databinding.ActivityMyHistoryDetailBinding
import com.runnect.runnect.presentation.mypage.history.MyHistoryActivity
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.custom.dialog.CommonDialogFragment
import com.runnect.runnect.util.custom.dialog.CommonDialogText
import com.runnect.runnect.util.custom.popup.PopupItem
import com.runnect.runnect.util.custom.popup.RunnectPopupMenu
import com.runnect.runnect.util.extension.getCompatibleSerializableExtra
import com.runnect.runnect.util.extension.hideKeyboard
import com.runnect.runnect.util.extension.showKeyboard
import com.runnect.runnect.util.extension.showSnackbar
import com.runnect.runnect.util.extension.showToast
import com.runnect.runnect.util.mode.ScreenMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyHistoryDetailActivity :
    BindingActivity<ActivityMyHistoryDetailBinding>(R.layout.activity_my_history_detail) {
    private val viewModel: MyHistoryDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this@MyHistoryDetailActivity
        binding.vm = viewModel

        initLayout()
        addListener()
        addObserver()
        registerBackPressedCallback()
    }

    private fun initLayout() {
        val bundle = intent.getBundleExtra(HISTORY_INTENT_KEY)
        val runningHistory: HistoryInfoDTO? =
            bundle?.getCompatibleSerializableExtra(HISTORY_BUNDLE_KEY)
        initRunningHistory(runningHistory)
        enterReadMode()
    }

    private fun initRunningHistory(historyDto: HistoryInfoDTO?) {
        if (historyDto != null) {
            binding.historyDto = historyDto
            viewModel.apply {
                updateHistoryTitle(historyDto.title)
                updateHistoryId(historyDto.id)
            }
        }
    }

    private fun enterReadMode() {
        viewModel.updateCurrentScreenMode(ScreenMode.ReadOnlyMode)
        updateTitleInputType()
        updateMoreButtonVisibility(true)
        updateConstraintForReadMode()
    }

    private fun enterEditMode() {
        viewModel.updateCurrentScreenMode(ScreenMode.EditMode)
        updateTitleInputType()
        updateMoreButtonVisibility(false)
        updateConstraintForEditMode()

        // 중간에 수정을 취소하면 원래 제목으로 되돌리기 위해
        // 현재 제목을 뷰모델에 저장해둔다.
        viewModel.saveCurrentTitle()
    }

    private fun updateMoreButtonVisibility(isVisible: Boolean) {
        binding.ivShowMore.isVisible = isVisible
    }

    private fun addListener() {
        initBackButtonClickListener()
        initShowMoreButtonClickListener()
        initEditFinishButtonClickListener()
        initTitleEditorActionDoneListener()
    }

    private fun initBackButtonClickListener() {
        binding.ivBackBtn.setOnClickListener {
            handleBackButtonByCurrentScreenMode()
        }
    }

    private fun initShowMoreButtonClickListener() {
        binding.ivShowMore.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun initEditFinishButtonClickListener() {
        binding.btnMyHistoryDetailEditFinish.setOnClickListener {
            viewModel.patchHistoryTitle()
        }
    }

    private fun initTitleEditorActionDoneListener() {
        binding.etCourseTitle.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.patchHistoryTitle()
                    hideKeyboard(binding.etCourseTitle)
                    return true
                }
                return false
            }
        })
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackButtonByCurrentScreenMode()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun handleBackButtonByCurrentScreenMode() {
        when (viewModel.currentScreenMode) {
            is ScreenMode.ReadOnlyMode -> navigateToPreviousScreen()
            is ScreenMode.EditMode -> showStopEditingDialog()
        }
    }

    private fun showPopupMenu(anchorView: View) {
        val popupItems = listOf(
            PopupItem(R.drawable.ic_detail_more_edit, getString(R.string.popup_menu_item_edit)),
            PopupItem(R.drawable.ic_detail_more_delete, getString(R.string.popup_menu_item_delete))
        )

        RunnectPopupMenu(anchorView.context, popupItems) { _, _, pos ->
            when (pos) {
                0 -> enterEditMode()
                1 -> showHistoryDeleteDialog()
            }
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun RunnectPopupMenu.showCustomPosition(anchorView: View) {
        showAsDropDown(anchorView, POPUP_MENU_X_OFFSET, POPUP_MENU_Y_OFFSET, Gravity.END)
    }

    private fun navigateToPreviousScreen() {
        // todo: 일부 아이템에 대해서만 수정/삭제가 반영되고 전체는 리프레시 하지 않도록 수정하기
        Intent(this, MyHistoryActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(this)
        }
    }

    private fun addObserver() {
        setupHistoryDeleteStateObserver()
        setupTitlePatchStateObserver()
    }

    private fun setupHistoryDeleteStateObserver() {
        viewModel.historyDeleteState.observe(this) { state ->
            when (state) {
                is UiStateV2.Loading -> showLoadingProgressBar()

                is UiStateV2.Success -> {
                    dismissLoadingProgressBar()
                    navigateToPreviousScreen()
                }

                is UiStateV2.Failure -> {
                    dismissLoadingProgressBar()
                    showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
        }
    }

    private fun setupTitlePatchStateObserver() {
        viewModel.titlePatchState.observe(this) { state ->
            when (state) {
                is UiStateV2.Loading -> showLoadingProgressBar()

                is UiStateV2.Success -> {
                    dismissLoadingProgressBar()
                    enterReadMode()

                    val response = state.data ?: return@observe
                    val newTitle = response.record.title
                    viewModel.updateHistoryTitle(newTitle)

                    showToast(getString(R.string.course_detail_title_edit_success_msg))
                }

                is UiStateV2.Failure -> {
                    dismissLoadingProgressBar()
                }

                else -> {}
            }
        }
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
            btnMyHistoryDetailEditFinish.isVisible = false
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
            btnMyHistoryDetailEditFinish.isVisible = true
        }
    }

    private fun updateTitleInputType() {
        when (viewModel.currentScreenMode) {
            is ScreenMode.ReadOnlyMode -> {
                binding.etCourseTitle.inputType = InputType.TYPE_NULL
            }

            is ScreenMode.EditMode -> {
                binding.etCourseTitle.apply {
                    inputType = InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
                    showKeyboard(this@MyHistoryDetailActivity)
                }
            }
        }
    }

    private fun showHistoryDeleteDialog() {
        val dialog = CommonDialogFragment.newInstance(
            CommonDialogText(
                getString(R.string.dialog_my_history_detail_delete_desc),
                getString(R.string.dialog_course_detail_delete_no),
                getString(R.string.dialog_course_detail_delete_yes)
            ),
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = { viewModel.deleteHistory() }
        )
        dialog.show(supportFragmentManager, TAG_MY_HISTORY_DELETE_DIALOG)
    }

    private fun showStopEditingDialog() {
        val dialog = CommonDialogFragment.newInstance(
            CommonDialogText(
                getString(R.string.dialog_my_history_detail_stop_editing_desc),
                getString(R.string.dialog_course_detail_stop_editing_no),
                getString(R.string.dialog_course_detail_stop_editing_yes)
            ),
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = {
                // 편집 모드 -> 뒤로가기 버튼 -> 편집 중단 확인 -> 뷰에 원래 제목으로 보여줌.
                viewModel.restoreOriginalTitle()
                enterReadMode()
            }
        )
        dialog.show(supportFragmentManager, TAG_MY_HISTORY_EDIT_DIALOG)
    }

    companion object {
        private const val HISTORY_BUNDLE_KEY = "historyDataBundle"
        private const val HISTORY_INTENT_KEY = "historyData"
        private const val POPUP_MENU_X_OFFSET = 17
        private const val POPUP_MENU_Y_OFFSET = -10
        private const val TAG_MY_HISTORY_DELETE_DIALOG = "MY_HISTORY_DELETE_DIALOG"
        private const val TAG_MY_HISTORY_EDIT_DIALOG = "MY_HISTORY_EDIT_DIALOG"
    }
}