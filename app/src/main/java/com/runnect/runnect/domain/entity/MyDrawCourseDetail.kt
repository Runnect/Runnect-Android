package com.runnect.runnect.domain.entity

data class MyDrawCourseDetail(
    val title: String,
    val imgUrl: String,
    val isNowUser: Boolean,
    val distance: Float,
    val courseId: Int,
    val path: List<List<Double>>,
    val departureName: String,
)