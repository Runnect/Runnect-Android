package com.runnect.runnect.util

import androidx.recyclerview.widget.DiffUtil
import com.runnect.runnect.data.dto.UserUploadCourseDTO

class CourseUploadedDiffUtilItemCallback :DiffUtil.ItemCallback<UserUploadCourseDTO>() {

    override fun areItemsTheSame(oldItem: UserUploadCourseDTO, newItem: UserUploadCourseDTO): Boolean {
        return oldItem.id== newItem.id
    }

    override fun areContentsTheSame(oldItem: UserUploadCourseDTO, newItem: UserUploadCourseDTO): Boolean {
        return oldItem==newItem
    }
}