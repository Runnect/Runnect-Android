package com.runnect.runnect.presentation.mypage.reward.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.runnect.runnect.data.dto.RewardStampDTO
import com.runnect.runnect.databinding.ItemMypageRewardBinding
import com.runnect.runnect.util.callback.diff.ItemDiffCallback

class MyRewardAdapter :
    ListAdapter<RewardStampDTO, MyRewardAdapter.ItemViewHolder>(diffUtil) {
    class ItemViewHolder(private val binding: ItemMypageRewardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: RewardStampDTO) {
            with(binding) {
                Glide.with(itemView)
                    .load(data.img)
                    .thumbnail(0.3f)
                    .into(ivItemMyPageRewardCircleFrame)
                tvItemMyPageRewardCondition.text = data.condition
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemMypageRewardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    companion object {
        private val diffUtil = ItemDiffCallback<RewardStampDTO>(
            onItemsTheSame = { old, new -> old === new },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
