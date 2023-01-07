package com.runnect.runnect.util

import androidx.recyclerview.widget.DiffUtil
import com.runnect.runnect.data.model.CourseInfoDTO
import com.runnect.runnect.data.model.CourseLoadInfoDTO

class CourseLoadInfoDiffUtilItemCallback :DiffUtil.ItemCallback<CourseLoadInfoDTO>() {

    override fun areItemsTheSame(oldItem: CourseLoadInfoDTO, newItem: CourseLoadInfoDTO): Boolean {
        return oldItem.id== newItem.id
    }

    override fun areContentsTheSame(oldItem:CourseLoadInfoDTO, newItem: CourseLoadInfoDTO): Boolean {
        return oldItem==newItem
    }
}