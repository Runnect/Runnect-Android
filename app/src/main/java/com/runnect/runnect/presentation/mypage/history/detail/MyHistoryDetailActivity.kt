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
import com.runnect.runnect.util.PopupItem
import com.runnect.runnect.util.RunnectPopupMenu
import com.runnect.runnect.util.extension.customGetSerializable
import com.runnect.runnect.util.extension.setCustomDialog
import com.runnect.runnect.util.extension.setDialogClickListener
import com.runnect.runnect.util.extension.setEditBottomSheet
import com.runnect.runnect.util.extension.setEditBottomSheetClickListener
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_edit_mode.layout_delete_frame
import kotlinx.android.synthetic.main.custom_dialog_edit_mode.layout_edit_frame
import kotlinx.android.synthetic.main.fragment_bottom_sheet.btn_delete_yes
import timber.log.Timber

@AndroidEntryPoint
class MyHistoryDetailActivity :
    BindingActivity<ActivityMyHistoryDetailBinding>(R.layout.activity_my_history_detail) {
    private lateinit var historyData: HistoryInfoDTO
    private lateinit var editBottomSheet: BottomSheetDialog
    private lateinit var deleteDialog: AlertDialog
    private lateinit var editInterruptDialog: AlertDialog
    private val viewModel: MyHistoryDetailViewModel by viewModels()
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleIsEdited(viewModel.titleForInterruption.isEmpty())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        addListener()
        addObserver()
        initEditBottomSheet()
        setEditBottomSheetClickEvent()
        initDeleteDialog()
        setDeleteDialogClickEvent()
        initEditInterruptedDialog()
        setEditInterruptedDialog()
    }

    private fun initLayout() {
        val bundle = intent.getBundleExtra(HISTORY_INTENT_KEY)
        historyData = bundle?.customGetSerializable(HISTORY_BUNDLE_KEY)!!
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
            //editBottomSheet.show()
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
                0 -> {
                    /** 수정하기 */
                }

                1 -> {
                    /** 삭제하기 */
                }
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
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        } else {
            val intent = Intent(this, MyHistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

    }

    private fun addObserver() {
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
        viewModel.deleteState.observe(this) { state ->
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
        viewModel.editState.observe(this) { state ->
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
        viewModel.titleForInterruption = viewModel.title.value.toString()
        enableEditTitle()
        updateConstraintForEditMode()
        editBottomSheet.dismiss()
        binding.ivShowMore.isVisible = false
    }

    private fun enterReadMode() {
        viewModel.editMode.value = READ_MODE
        disableEditTitle()
        updateConstraintForReadMode()
        binding.ivShowMore.isVisible = true
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

    private fun enableEditTitle() {
        binding.etCourseTitle.inputType = InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
    }

    private fun disableEditTitle() {
        binding.etCourseTitle.inputType = InputType.TYPE_NULL
    }

    private fun initEditBottomSheet() {
        editBottomSheet = setEditBottomSheet()
        setEditBottomSheetClickEvent()
    }

    private fun setEditBottomSheetClickEvent() {
        editBottomSheet.setEditBottomSheetClickListener { which ->
            when (which) {
                editBottomSheet.layout_edit_frame -> {
                    enterEditMode()
                }

                editBottomSheet.layout_delete_frame -> {
                    editBottomSheet.dismiss()
                    deleteDialog.show()
                }
            }
        }
    }

    private fun initDeleteDialog() {
        deleteDialog = setCustomDialog(
            layoutInflater = layoutInflater,
            view = binding.root,
            description = DELETE_DIALOG_DESC,
            yesBtnText = DELETE_DIALOG_YES_BTN
        )
    }

    private fun setDeleteDialogClickEvent() {
        deleteDialog.setDialogClickListener { which ->
            when (which) {
                deleteDialog.btn_delete_yes -> {
                    viewModel.deleteHistory()
                }
            }

        }
    }

    private fun initEditInterruptedDialog() {
        editInterruptDialog = setCustomDialog(
            layoutInflater = layoutInflater,
            view = binding.root,
            description = EDIT_INTERRUPT_DIALOG_DESC,
            yesBtnText = EDIT_INTERRUPT_DIALOG_YES_BTN,
            noBtnText = EDIT_INTERRUPT_DIALOG_NO_BTN
        )
    }

    private fun setEditInterruptedDialog() {
        editInterruptDialog.setDialogClickListener { which ->
            when (which) {
                editInterruptDialog.btn_delete_yes -> {
                    enterReadMode()
                    viewModel.title.value = viewModel.titleForInterruption
                }
            }
        }
    }

    companion object {
        const val HISTORY_BUNDLE_KEY = "historyDataBundle"
        const val HISTORY_INTENT_KEY = "historyData"
        const val EDIT_MODE = "editMode"
        const val READ_MODE = "readMode"
        const val DELETE_DIALOG_DESC = "러닝 기록을 정말로 삭제하시겠어요?"
        const val DELETE_DIALOG_YES_BTN = "삭제하기"
        const val EDIT_INTERRUPT_DIALOG_DESC = "     러닝 기록 수정을 종료할까요?\n종료 시 수정 내용이 반영되지 않아요."
        const val EDIT_INTERRUPT_DIALOG_YES_BTN = "예"
        const val EDIT_INTERRUPT_DIALOG_NO_BTN = "아니오"
        private const val POPUP_MENU_X_OFFSET = 17
        private const val POPUP_MENU_Y_OFFSET = -10
    }
}