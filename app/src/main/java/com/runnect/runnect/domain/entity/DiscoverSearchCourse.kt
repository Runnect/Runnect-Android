package com.runnect.runnect.domain.entity

data class DiscoverSearchCourse(
    val id: Int,
    val courseId: Int,
    var title: String,
    val image: String,
    var scrap: Boolean,
    val departure: String,
)
