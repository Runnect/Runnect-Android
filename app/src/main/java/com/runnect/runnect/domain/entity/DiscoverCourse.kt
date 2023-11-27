package com.runnect.runnect.domain.entity

data class DiscoverCourse(
    val id: Int,
    val courseId: Int,
    val title: String,
    val image: String,
    val scrap: Boolean,
    val departure: String,
)