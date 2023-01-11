package com.runnect.runnect.util

import androidx.recyclerview.widget.DiffUtil
import com.runnect.runnect.data.dto.RewardStampDTO

class RewardStampDiffUtilItemCallback : DiffUtil.ItemCallback<RewardStampDTO>() {

    override fun areItemsTheSame(oldItem: RewardStampDTO, newItem: RewardStampDTO): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: RewardStampDTO, newItem: RewardStampDTO): Boolean {
        return oldItem == newItem
    }
}