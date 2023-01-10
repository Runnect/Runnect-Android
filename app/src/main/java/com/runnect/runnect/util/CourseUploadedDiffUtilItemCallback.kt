package com.runnect.runnect.util

import androidx.recyclerview.widget.DiffUtil
import com.runnect.runnect.data.dto.UploadedCourseDTO

class CourseUploadedDiffUtilItemCallback :DiffUtil.ItemCallback<UploadedCourseDTO>() {

    override fun areItemsTheSame(oldItem: UploadedCourseDTO, newItem: UploadedCourseDTO): Boolean {
        return oldItem.id== newItem.id
    }

    override fun areContentsTheSame(oldItem: UploadedCourseDTO, newItem: UploadedCourseDTO): Boolean {
        return oldItem==newItem
    }
}