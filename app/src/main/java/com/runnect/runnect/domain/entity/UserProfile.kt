package com.runnect.runnect.domain.entity

data class UserProfile(
    val nickname: String,
    val level: Int,
    val levelPercent: Int,
    val latestStamp: String,
    val courseData: List<UserCourse>
)

data class UserCourse(
    val publicCourseId: Int,
    val courseId: Int,
    val title: String,
    val image: String,
    val departure: Departure,
    var scrapTF: Boolean,
)

data class Departure(
    val region: String,
    val city: String,
    val town: String,
    val detail: String?,
    val name: String
)
