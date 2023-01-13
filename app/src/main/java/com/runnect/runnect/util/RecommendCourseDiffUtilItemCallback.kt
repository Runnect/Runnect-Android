package com.runnect.runnect.util

import androidx.recyclerview.widget.DiffUtil
import com.runnect.runnect.data.dto.RecommendCourseDTO

class RecommendCourseDiffUtilItemCallback :DiffUtil.ItemCallback<RecommendCourseDTO>() {

    override fun areItemsTheSame(oldItem: RecommendCourseDTO, newItem: RecommendCourseDTO): Boolean {
        return oldItem.id== newItem.id
    }

    override fun areContentsTheSame(oldItem: RecommendCourseDTO, newItem: RecommendCourseDTO): Boolean {
        return oldItem==newItem
    }
}