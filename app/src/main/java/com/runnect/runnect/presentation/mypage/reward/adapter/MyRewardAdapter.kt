package com.runnect.runnect.presentation.mypage.reward.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.RewardStampDTO
import com.runnect.runnect.databinding.ItemMypageRewardBinding
import com.runnect.runnect.util.RewardStampDiffUtilItemCallback
import timber.log.Timber

class MyRewardAdapter(context: Context) :
    ListAdapter<RewardStampDTO, ItemViewHolder>(RewardStampDiffUtilItemCallback()) {
    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemMypageRewardBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

}

class ItemViewHolder(private val binding: ItemMypageRewardBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(data: RewardStampDTO) {
        binding.tvItemMyPageRewardCondition.text = "코스 ${data.num} 개"
        if (data.isLock) {
            Timber.d("${data.id} false")
            binding.ivItemMyPageRewardCircleFrame.setImageResource(R.drawable.mypage_img_stamp_lock)
        } else {
            Timber.d("${data.id} true")
            binding.ivItemMyPageRewardCircleFrame.setImageResource(data.img)
        }
    }
}