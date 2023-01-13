package com.runnect.runnect.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseUserUploadCourse(
    val `data`: UserUploadData,
    val message: String,
    val status: Int,
    val success: Boolean
)
@Serializable
data class UserX(
    val machineId: String
)
@Serializable
data class PublicCourse(
    val courseId: Int,
    val departure: UserUploadDeparture,
    val id: Int,
    val image: String,
    val title: String
)
@Serializable
data class UserUploadDeparture(
    val city: String,
    val region: String
)
@Serializable
data class UserUploadData(
    val publicCourses: List<PublicCourse>,
    val user: UserX
)