package com.runnect.runnect.presentation.mypage.history

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
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyHistoryActivity : BindingActivity<ActivityMyHistoryBinding>(R.layout.activity_my_history),OnHistoryItemClick {
    private val viewModel: MyHistoryViewModel by viewModels()

    private lateinit var adapter: MyHistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        //활동기록 날짜 형식 바꿀 때에는 공백 기준으로 뒤에 건 날려버리고 앞에건 -를 .으로 replace하면 될듯!
        //이미지는 bindingAdapter쓰고.
        initLayout()
        addListener()
        addObserver()
    }

    private fun initLayout() {
        viewModel.getRecord()
        binding.rvMyPageHistory.layoutManager = LinearLayoutManager(this)
        binding.rvMyPageHistory.addItemDecoration(RecyclerOffsetDecorationHeight(this, 10))
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
            //viewmodel의 itemsToDeleted에 저장되어있는 데이터를 이용하여 삭제 API 호출
            viewModel.clearItemsToDelete()
        }

        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
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
                    initAdapter()
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.errorMessage.value}")
                }
            }
        }

        viewModel.editMode.observe(this){ editMode->
            with(binding.btnMyPageHistoryEditHistory){
                if(editMode){
                    this.text = EDIT_CANCEL
                    binding.tvMyPageHistoryTotalCourseCount.text = "기록 선택"
                }
                else{
                    this.text = EDIT_MODE
                    binding.tvMyPageHistoryTotalCourseCount.text = viewModel.getHistoryCount()
                    adapter.clearSelection()
                    viewModel.clearItemsToDelete()
                }
            }
        }
        viewModel.selectedItemsCount.observe(this) { count ->
            with(binding.tvMyPageHistoryDelete){
                text = getTextDeleteButton(count)
            }
        }
    }

    private fun initAdapter() {
        adapter = MyHistoryAdapter(this,this).apply {
            submitList(viewModel.historyItem)
        }
        binding.rvMyPageHistory.adapter = adapter
    }
    companion object{
        const val EDIT_CANCEL = "취소"
        const val EDIT_MODE = "편집"
    private fun getTextDeleteButton(count:Int): String {
        return if (count == 0) {
            "삭제하기"
        } else {
            "삭제하기(${count})"
        }
    }

    override fun selectItem(s:String): Boolean {
        return if(viewModel.editMode.value == true){
            viewModel.addItemToDelete(s)
            showToast("기록 선택 완료")
            true
        } else{
            //매개변수로 아이템 정보를 넘겨받아 기록 조회 액티비티 이동
            showToast("기록 조회 액티비티 이동")
            false
        }
    }
}