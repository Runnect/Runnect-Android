package com.runnect.runnect.domain.entity

data class CourseDetail(
    val nickname: String,
    val level: String,
    val stampId: String,
    val isNowUser: Boolean,
    val id: Int,
    val courseId: Int,
    val scrap: Boolean,
    val scrapCount: String,
    val image: String,
    val title: String,
    val description: String,
    val path: List<List<Double>>,
    val distance: String,
    val departure: String,
)
