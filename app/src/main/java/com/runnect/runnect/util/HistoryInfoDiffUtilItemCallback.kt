package com.runnect.runnect.util

import androidx.recyclerview.widget.DiffUtil
import com.runnect.runnect.data.dto.RecordInfoDTO

class HistoryInfoDiffUtilItemCallback :DiffUtil.ItemCallback<RecordInfoDTO>() {

    override fun areItemsTheSame(oldItem: RecordInfoDTO, newItem: RecordInfoDTO): Boolean {
        return oldItem.id== newItem.id
    }

    override fun areContentsTheSame(oldItem: RecordInfoDTO, newItem: RecordInfoDTO): Boolean {
        return oldItem==newItem
    }
}