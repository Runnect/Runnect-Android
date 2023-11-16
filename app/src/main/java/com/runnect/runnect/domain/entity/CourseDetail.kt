package com.runnect.runnect.domain.entity

data class CourseDetail(
    val stampId: String,
    val level: String,
    val nickname: String,
    val isNowUser: Boolean,
    val id: Int,
    val courseId: Int,
    val departure: String,
    val description: String,
    val distance: String,
    val image: String,
    val isScrap: Boolean,
    val title: String,
    val path: List<List<Double>>
)
