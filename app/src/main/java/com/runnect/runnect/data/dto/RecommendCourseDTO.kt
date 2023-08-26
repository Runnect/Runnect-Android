package com.runnect.runnect.data.dto

data class RecommendCourseDTO(
    val pageNo: Int,
    val courseId: Int,
    val departure: String,
    val id: Int,
    val image: String,
    val scrap: Boolean,
    val title: String
)
