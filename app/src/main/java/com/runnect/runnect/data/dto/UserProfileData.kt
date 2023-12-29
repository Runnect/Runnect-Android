package com.runnect.runnect.data.dto

data class UserProfileData(
    val nickname: String,
    val level: Int,
    val levelPercent: Int,
    val latestStamp: String,
    val courseData: List<UserCourseData>
)

data class UserCourseData(
    val publicCourseId: Int,
    val courseId: Int,
    val title: String,
    val image: String,
    val departure: DepartureData,
    var scrapTF: Boolean,
)

data class DepartureData(
    val region: String,
    val city: String,
    val town: String,
    val detail: String?,
    val name: String
)
