package com.runnect.runnect.util

import androidx.recyclerview.widget.DiffUtil
import com.runnect.runnect.data.model.CourseInfoDTO

class CourseInfoDiffUtilItemCallback :DiffUtil.ItemCallback<CourseInfoDTO>() {

    override fun areItemsTheSame(oldItem: CourseInfoDTO, newItem: CourseInfoDTO): Boolean {
        return oldItem.id== newItem.id
    }

    override fun areContentsTheSame(oldItem:CourseInfoDTO, newItem: CourseInfoDTO): Boolean {
        return oldItem==newItem
    }
}