package com.runnect.runnect.presentation.mypage.history

import android.content.ContentValues
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.RecordInfoDTO
import com.runnect.runnect.databinding.ActivityMyHistoryBinding
import com.runnect.runnect.presentation.mypage.history.adapter.MyHistoryAdapter
import com.runnect.runnect.presentation.mypage.history.adapter.MyHistoryViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.RecyclerOffsetDecorationHeight
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyHistoryActivity : BindingActivity<ActivityMyHistoryBinding>(R.layout.activity_my_history) {
    private val viewModel: MyHistoryViewModel by viewModels()

    private lateinit var adapter: MyHistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        viewModel.getRecord()

        //활동기록 날짜 형식 바꿀 때에는 공백 기준으로 뒤에 건 날려버리고 앞에건 -를 .으로 replace하면 될듯!
        //페이스 이상한 따옴표는 피그마에서 글자 복사해오면 된다.
        //이미지는 bindingAdapter쓰고.
        initLayout()
        addListener()
        addObserver()
    }

    private fun initLayout() {
        binding.rvMyPageHistory.layoutManager = LinearLayoutManager(this)
        binding.rvMyPageHistory.addItemDecoration(RecyclerOffsetDecorationHeight(this, 10))
    }

    private fun addListener() {
        binding.ivMyPageHistoryBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


    private fun addObserver() {

        viewModel.recordState.observe(this) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false //visible 옵션으로 처리하는 게 맞나
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    initAdapter()
                }
                UiState.Failure -> Timber.tag(ContentValues.TAG)
                    .d("Failure : ${viewModel.errorMessage.value}")
            }
        }


    }

    private fun initAdapter() {
        adapter = MyHistoryAdapter(this).apply {
            submitList(viewModel.recordList)
        }
        binding.rvMyPageHistory.adapter = adapter
    }
}