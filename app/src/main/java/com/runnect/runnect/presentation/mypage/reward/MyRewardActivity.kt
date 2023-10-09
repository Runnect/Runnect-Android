package com.runnect.runnect.presentation.mypage.reward

import android.content.ContentValues
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.RewardStampDTO
import com.runnect.runnect.databinding.ActivityMyRewardBinding
import com.runnect.runnect.presentation.mypage.reward.adapter.MyRewardAdapter
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.GridSpacingItemDecoration
import com.runnect.runnect.util.extension.getStampResId
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyRewardActivity : BindingActivity<ActivityMyRewardBinding>(R.layout.activity_my_reward) {
    private val viewModel: MyRewardViewModel by viewModels()
    private lateinit var adapter: MyRewardAdapter
    private var stampListForIndex =
        listOf("c1", "c2", "c3", "s1", "s2", "s3", "u1", "u2", "u3", "r1", "r2", "r3")

    private val stampImgList = mutableListOf(
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_0_0),
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_0_1),
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_0_2),
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_1_0),
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_1_1),
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_1_2),
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_2_0),
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_2_1),
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_2_2),
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_3_0),
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_3_1),
        RewardStampDTO(R.drawable.mypage_img_stamp_lock, MISSION_ROW_COLUMN_3_2),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        viewModel.getStampList()

        initRecyclerView()
        initBackButtonClickListener()
        setupGetStampListStateObserver()
        registerBackPressedCallback()
    }

    private fun initRecyclerView() {
        binding.rvMyPageRewardStamps.setHasFixedSize(true)
        binding.rvMyPageRewardStamps.layoutManager = GridLayoutManager(this, 3)
        binding.rvMyPageRewardStamps.addItemDecoration(GridSpacingItemDecoration(this, 3, 28, 28))
    }

    private fun initBackButtonClickListener() {
        binding.ivMyPageRewardBack.setOnClickListener {
            navigateToPreviousScreen()
        }
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToPreviousScreen()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun navigateToPreviousScreen() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun setupGetStampListStateObserver() {
        viewModel.getStampListState.observe(this) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false //visible 옵션으로 처리하는 게 맞나
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    initAdapter()
                }

                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG).d("Failure : ${viewModel.errorMessage.value}")
                }
            }
        }
    }

    private fun initAdapter() {
        var index = 0
        if (viewModel.stampList.isNotEmpty()) {
            for (i in viewModel.stampList) {
                index = stampListForIndex.indexOf(i)
                val stampResId = this.getStampResId(
                    stampId = i,
                    resNameParam = RES_NAME,
                    resType = RES_STAMP_TYPE,
                    packageName = this.packageName
                )
                stampImgList[index].img = stampResId
            }
        }
        adapter = MyRewardAdapter(this).apply {
            submitList(
                stampImgList
            )
        }
        binding.rvMyPageRewardStamps.adapter = this.adapter
    }

    companion object {
        const val RES_NAME = "mypage_img_stamp_"
        const val RES_STAMP_TYPE = "drawable"
        const val MISSION_ROW_COLUMN_0_0 = "그린 코스 1개"
        const val MISSION_ROW_COLUMN_0_1 = "그린 코스 10개"
        const val MISSION_ROW_COLUMN_0_2 = "그린 코스 30개"
        const val MISSION_ROW_COLUMN_1_0 = "스크랩 1회"
        const val MISSION_ROW_COLUMN_1_1 = "스크랩 20회"
        const val MISSION_ROW_COLUMN_1_2 = "스크랩 40회"
        const val MISSION_ROW_COLUMN_2_0 = "업로드 1회"
        const val MISSION_ROW_COLUMN_2_1 = "업로드 10회"
        const val MISSION_ROW_COLUMN_2_2 = "업로드 30회"
        const val MISSION_ROW_COLUMN_3_0 = "달리기 1회"
        const val MISSION_ROW_COLUMN_3_1 = "달리기 15회"
        const val MISSION_ROW_COLUMN_3_2 = "달리기 30회"
    }
}


