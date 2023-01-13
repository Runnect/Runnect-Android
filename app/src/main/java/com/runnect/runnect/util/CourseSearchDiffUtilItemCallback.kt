package com.runnect.runnect.util

import androidx.recyclerview.widget.DiffUtil
import com.runnect.runnect.data.dto.CourseSearchDTO

class CourseSearchDiffUtilItemCallback :DiffUtil.ItemCallback<CourseSearchDTO>() {
    override fun areItemsTheSame(oldItem: CourseSearchDTO, newItem: CourseSearchDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CourseSearchDTO, newItem: CourseSearchDTO): Boolean {
        return oldItem == newItem
    }
}