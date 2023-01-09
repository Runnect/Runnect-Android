package com.runnect.runnect.util

import androidx.recyclerview.widget.DiffUtil
import com.runnect.runnect.data.model.RewardStampDTO

class RewardStampDiffUtilItemCallback : DiffUtil.ItemCallback<RewardStampDTO>() {

    override fun areItemsTheSame(oldItem: RewardStampDTO, newItem: RewardStampDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RewardStampDTO, newItem: RewardStampDTO): Boolean {
        return oldItem == newItem
    }
}