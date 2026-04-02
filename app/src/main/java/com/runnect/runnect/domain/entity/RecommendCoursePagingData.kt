package com.runnect.runnect.domain.entity

data class RecommendCoursePagingData(
    val isEnd: Boolean,
    val recommendCourses: List<DiscoverMultiViewItem.RecommendCourse>
)