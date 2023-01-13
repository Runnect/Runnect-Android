package com.runnect.runnect.presentation.mypage.reward.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.runnect.runnect.data.dto.RewardStampDTO
import com.runnect.runnect.databinding.ItemMypageRewardBinding
import com.runnect.runnect.util.RewardStampDiffUtilItemCallback

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
        binding.ivItemMyPageRewardCircleFrame.load(data.img)
        binding.tvItemMyPageRewardCondition.text = data.condition
    }
}