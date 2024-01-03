package com.runnect.runnect.data.dto

data class ProfileCourseData(
    val publicCourseId: Int,
    val courseId: Int,
    val title: String,
    val image: String,
    val departure: DepartureData,
    val scrapTF: Boolean
)

data class DepartureData(
    val region: String,
    val city: String,
    val town: String,
    val detail: String?,
    val name: String
)
