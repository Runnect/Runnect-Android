package com.runnect.runnect.util

import androidx.recyclerview.widget.DiffUtil
import com.runnect.runnect.data.dto.HistoryInfoDTO

class HistoryInfoDiffUtilItemCallback :DiffUtil.ItemCallback<HistoryInfoDTO>() {

    override fun areItemsTheSame(oldItem: HistoryInfoDTO, newItem: HistoryInfoDTO): Boolean {
        return oldItem.id== newItem.id
    }

    override fun areContentsTheSame(oldItem: HistoryInfoDTO, newItem: HistoryInfoDTO): Boolean {
        return oldItem==newItem
    }
}