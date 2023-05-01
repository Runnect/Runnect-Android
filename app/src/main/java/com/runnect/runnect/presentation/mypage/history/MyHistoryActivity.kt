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
                }
                else{
                    this.text = EDIT_MODE
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = MyHistoryAdapter(this).apply {
            submitList(viewModel.historyItem)
        }
        binding.rvMyPageHistory.adapter = adapter
    }
    companion object{
        const val EDIT_CANCEL = "취소"
        const val EDIT_MODE = "편집"
    }
}