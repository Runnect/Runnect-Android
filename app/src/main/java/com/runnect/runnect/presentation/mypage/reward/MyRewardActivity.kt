package com.runnect.runnect.presentation.mypage.reward

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.RewardStampDTO
import com.runnect.runnect.databinding.ActivityMyRewardBinding
import com.runnect.runnect.presentation.mypage.reward.adapter.MyRewardAdapter
import com.runnect.runnect.util.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRewardActivity : BindingActivity<ActivityMyRewardBinding>(R.layout.activity_my_reward) {
    private val adapter by lazy {
        MyRewardAdapter(this).apply {
            submitList(
                listOf(
                    RewardStampDTO(1,5, R.drawable.mypage_img_stamp_c1,false),
                    RewardStampDTO(2,10,R.drawable.mypage_img_stamp_c2,false),
                    RewardStampDTO(3,15,R.drawable.mypage_img_stamp_c3,true),
                    RewardStampDTO(4,5,R.drawable.mypage_img_stamp_s1,false),
                    RewardStampDTO(5,10,R.drawable.mypage_img_stamp_s2,true),
                    RewardStampDTO(6,15,R.drawable.mypage_img_stamp_s3,true),
                    RewardStampDTO(7,5,R.drawable.mypage_img_stamp_u1,false),
                    RewardStampDTO(8,10,R.drawable.mypage_img_stamp_u2,false),
                    RewardStampDTO(9,15,R.drawable.mypage_img_stamp_u3,true),
                    RewardStampDTO(10,5,R.drawable.mypage_img_stamp_r1,false),
                    RewardStampDTO(11,10,R.drawable.mypage_img_stamp_r2,true),
                    RewardStampDTO(12,15,R.drawable.mypage_img_stamp_r3,true)
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        addListener()
    }

    private fun initLayout() {
        binding.rvMyPageRewardStamps.setHasFixedSize(true)
        binding.rvMyPageRewardStamps.layoutManager = GridLayoutManager(this, 3)
        binding.rvMyPageRewardStamps.addItemDecoration(GridSpacingItemDecoration(this,3, 28, 28))
        binding.rvMyPageRewardStamps.adapter = this.adapter
    }

    private fun addListener() {
        binding.ivMyPageRewardBack.setOnClickListener {
            finish()
        }
    }


}


