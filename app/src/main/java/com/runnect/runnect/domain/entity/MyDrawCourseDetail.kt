package com.runnect.runnect.domain.entity

data class MyDrawCourseDetail(
    var title: String,
    val imgUrl: String,
    val isNowUser: Boolean,
    val distance: Float,
    val courseId: Int,
    val path: List<List<Double>>,
    val departureName: String,
)