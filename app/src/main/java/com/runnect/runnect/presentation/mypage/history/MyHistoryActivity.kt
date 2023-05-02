package com.runnect.runnect.presentation.mypage.history

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityMyHistoryBinding
import com.runnect.runnect.presentation.mypage.history.adapter.MyHistoryAdapter
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.RecyclerOffsetDecorationHeight
import com.runnect.runnect.util.callback.OnHistoryItemClick
import com.runnect.runnect.util.extension.setCustomDialog
import com.runnect.runnect.util.extension.setDialogClickListener
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_delete.*
import timber.log.Timber

@AndroidEntryPoint
class MyHistoryActivity : BindingActivity<ActivityMyHistoryBinding>(R.layout.activity_my_history),
    OnHistoryItemClick {
    private val viewModel: MyHistoryViewModel by viewModels()
    private lateinit var adapter: MyHistoryAdapter
    private lateinit var dialog: AlertDialog
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        initLayout()
        addListener()
        addObserver()
        initDialog()
    }

    private fun initLayout() {
        viewModel.getRecord()
        binding.rvMyPageHistory.layoutManager = LinearLayoutManager(this)
        binding.rvMyPageHistory.addItemDecoration(RecyclerOffsetDecorationHeight(this, 10))
    }

    private fun initDialog() {
        dialog = setCustomDialog(layoutInflater, binding.root, DIALOG_DESC, DELETE_BTN)
    }

    private fun setDialogClickEvent() {
        dialog.setDialogClickListener { which ->
            when (which) {
                dialog.btn_delete_yes -> {
                    viewModel.deleteHistory()
                    dialog.dismiss()
                }
                dialog.btn_delete_no -> {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun addListener() {
        binding.ivMyPageHistoryBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        binding.cvHistoryMyPageDrawCourse.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
            startActivity(intent)
            finish()
        }
        binding.btnMyPageHistoryEditHistory.setOnClickListener {
            viewModel.convertMode()
            binding.tvMyPageHistoryDelete.isVisible = viewModel.editMode.value!!
        }
        binding.tvMyPageHistoryDelete.setOnClickListener {
            if (it.isActivated) {
                setDialogClickEvent()
                dialog.show()
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun addObserver() {
        viewModel.historyState.observe(this) {
            when (it) {
                UiState.Empty -> {
                    binding.indeterminateBar.isVisible = false
                    binding.layoutMyPageHistoryNoResult.isVisible = true
                    binding.constMyPageHistoryEditBar.isVisible = false
                }

                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    binding.layoutMyPageHistoryNoResult.isVisible = false
                    binding.constMyPageHistoryEditBar.isVisible = true
                    binding.tvMyPageHistoryTotalCourseCount.text = viewModel.getHistoryCount()
                    initAdapter()
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.errorMessage.value}")
                }
            }
        }
        viewModel.historyDeleteState.observe(this){
            when(it){
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    adapter.removeItems(viewModel.itemsToDelete)
                    adapter.clearSelection()
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.errorMessage.value}")
                }
                else ->{
                    binding.indeterminateBar.isVisible = false }
            }
        }
        viewModel.editMode.observe(this) { editMode ->
            with(binding) {
                if (editMode) {
                    btnMyPageHistoryEditHistory.text = EDIT_CANCEL
                    tvMyPageHistoryTotalCourseCount.text = CHOICE_MODE_DESC
                } else {
                    btnMyPageHistoryEditHistory.text = EDIT_MODE
                    tvMyPageHistoryTotalCourseCount.text = viewModel.getHistoryCount()
                    if(::adapter.isInitialized) adapter.clearSelection()
                    tvMyPageHistoryDelete.isVisible = viewModel.editMode.value!!
                    viewModel.clearItemsToDelete()
                }
            }
        }
        viewModel.selectedItemsCount.observe(this) { count ->
            setDeleteButton(count)
        }
        viewModel.selectCountMediator.observe(this) {}
    }
    private fun setDeleteButton(count:Int){
        with(binding.tvMyPageHistoryDelete) {
            isActivated = count != 0
            text = getTextDeleteButton(count)
        }
    }

    private fun initAdapter() {
        adapter = MyHistoryAdapter(this, this).apply {
            submitList(viewModel.historyItems)
        }
        binding.rvMyPageHistory.adapter = adapter
    }

    private fun getTextDeleteButton(count: Int): String {
        return if (count == 0) {
            DELETE_BTN
        } else {
            "$DELETE_BTN(${count})"
        }
    }


    override fun selectItem(id: Int): Boolean {
        return if (viewModel.editMode.value == true) {
            viewModel.modifyItemsToDelete(id)
            true
        } else {
            //매개변수로 아이템 정보를 넘겨받아 기록 조회 액티비티 이동
            showToast("기록 조회 액티비티 이동")
            false
        }
    }

    companion object {
        const val CHOICE_MODE_DESC = "기록 선택"
        const val EDIT_CANCEL = "취소"
        const val EDIT_MODE = "편집"
        const val DIALOG_DESC = "러닝 기록을 정말로 삭제하시겠어요?"
        const val DELETE_BTN = "삭제하기"
    }
}