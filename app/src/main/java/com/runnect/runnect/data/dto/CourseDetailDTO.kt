package com.runnect.runnect.data.dto

data class CourseDetailDTO(
    val stampId: Int,
    val level: String,
    val nickname: String,
    val courseId: Int,
    val departure: String,
    val description: String,
    val distance: String,
    val id: Int,
    val image: String,
    val scrap: Boolean,
    val title: String
)
