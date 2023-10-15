package com.runnect.runnect.presentation.mypage.history.detail

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.databinding.ActivityMyHistoryDetailBinding
import com.runnect.runnect.presentation.mypage.history.MyHistoryActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.custom.CommonDialogFragment
import com.runnect.runnect.util.custom.PopupItem
import com.runnect.runnect.util.custom.RunnectPopupMenu
import com.runnect.runnect.util.extension.getCompatibleSerializableExtra
import com.runnect.runnect.util.extension.setCustomDialog
import com.runnect.runnect.util.extension.setDialogButtonClickListener
import com.runnect.runnect.util.extension.setEditBottomSheet
import com.runnect.runnect.util.extension.setEditBottomSheetClickListener
import com.runnect.runnect.util.extension.showToast
import com.runnect.runnect.util.extension.stringOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_edit_mode.layout_delete_frame
import kotlinx.android.synthetic.main.custom_dialog_edit_mode.layout_edit_frame
import kotlinx.android.synthetic.main.fragment_bottom_sheet.btn_delete_yes
import timber.log.Timber

@AndroidEntryPoint
class MyHistoryDetailActivity :
    BindingActivity<ActivityMyHistoryDetailBinding>(R.layout.activity_my_history_detail) {
    // 러닝 기록 데이터
    private lateinit var historyData: HistoryInfoDTO

    // 더보기 버튼 클릭 시 등장하는 바텀시트
    private lateinit var detailMoreBottomSheet: BottomSheetDialog

    // 삭제 확인 다이얼로그
    private lateinit var deleteDialog: AlertDialog

    // 수정 중단 확인 다이얼로그
    private lateinit var editInterruptDialog: AlertDialog

    private val viewModel: MyHistoryDetailViewModel by viewModels()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // TODO: 이 함수가 어떤 역할을 하는 건가요?
            handleIsEdited(viewModel.titleForInterruption.isEmpty())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        addListener()
        addObserver()
//        initEditBottomSheet()
//        setEditBottomSheetClickEvent()
    }

    private fun initLayout() {
        val bundle = intent.getBundleExtra(HISTORY_INTENT_KEY)
        historyData = bundle?.getCompatibleSerializableExtra(HISTORY_BUNDLE_KEY)!!

        with(binding) {
            vm = viewModel
            viewModel.mapImg.value = historyData.img
            lifecycleOwner = this@MyHistoryDetailActivity
            enterReadMode()
        }

        setHistoryData()
    }

    private fun setHistoryData() {
        with(historyData) {
            viewModel.setTitle(title)
            viewModel.date = date
            viewModel.departure = location
            viewModel.distance = distance
            viewModel.time = time
            viewModel.pace = pace
            viewModel.historyIdToDelete = listOf(id)
        }
    }

    private fun addListener() {
        binding.ivShowMore.setOnClickListener { view ->
            //detailMoreBottomSheet.show()
            showPopupMenu(view)
        }

        binding.ivBackBtn.setOnClickListener {
            if (viewModel.editMode.value == EDIT_MODE) {
                editInterruptDialog.show()
            } else {
                handleIsEdited(viewModel.titleForInterruption.isEmpty())
            }
        }

        binding.tvHistoryEditFinish.setOnClickListener {
            viewModel.editHistoryTitle()
        }

        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun showPopupMenu(anchorView: View) {
        val popupItems = listOf(
            PopupItem(R.drawable.ic_detail_more_edit, getString(R.string.popup_menu_item_edit)),
            PopupItem(R.drawable.ic_detail_more_delete, getString(R.string.popup_menu_item_delete))
        )

        RunnectPopupMenu(anchorView.context, popupItems) { _, _, pos ->
            when (pos) {
                0 -> { /** 제목 수정 가능한 상태로 만들기 */ }
                1 -> showHistoryDeleteDialog()
            }
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun RunnectPopupMenu.showCustomPosition(anchorView: View) {
        showAsDropDown(anchorView, POPUP_MENU_X_OFFSET, POPUP_MENU_Y_OFFSET, Gravity.END)
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
        setupTitleValueObserver()
        setupHistoryDeleteStateObserver()
        setupTitleEditStateObserver()
    }

    private fun setupTitleValueObserver() {
        viewModel.title.observe(this) { title ->
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
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    val intent = Intent(this, MyHistoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }

                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG).d("Failure : ${viewModel.errorMessage.value}")
                }

                else -> {}
            }
        }
    }

    private fun setupTitleEditStateObserver() {
        viewModel.titleEditState.observe(this) { state ->
            when (state) {
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    enterReadMode()
                    viewModel.titleForInterruption = viewModel.title.value.toString()
                    showToast("수정이 완료되었습니다")
                }

                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG).d("Failure : ${viewModel.errorMessage.value}")
                }

                else -> {}
            }
        }
    }

    private fun enterEditMode() {
        viewModel.editMode.value = EDIT_MODE

        // 수정 모드에 진입하면서, 원래 제목을 뷰모델에 저장해둔다.
        viewModel.titleForInterruption = viewModel.title.value.toString()

        enableEditTitle()
        updateConstraintForEditMode()
        detailMoreBottomSheet.dismiss()
        binding.ivShowMore.isVisible = false
    }

    private fun enterReadMode() {
        viewModel.editMode.value = READ_MODE
        disableEditTitle()
        updateConstraintForReadMode()
        binding.ivShowMore.isVisible = true
    }

    // todo: https://m.blog.naver.com/lys1900/222030610067
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

    private fun enableEditTitle() {
        binding.etCourseTitle.inputType = InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
    }

    private fun disableEditTitle() {
        binding.etCourseTitle.inputType = InputType.TYPE_NULL
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

    private fun showEditFinishDialog() {
        val dialog = CommonDialogFragment(
            stringOf(R.string.dialog_my_history_detail_edit_finish_desc),
            stringOf(R.string.dialog_my_history_detail_edit_finish_no),
            stringOf(R.string.dialog_my_history_detail_edit_finish_yes),
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = {
                enterReadMode()

                // 원래 제목으로 다시 복구한다.
                viewModel.title.value = viewModel.titleForInterruption
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