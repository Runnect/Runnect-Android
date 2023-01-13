package com.runnect.runnect.data.dto.response
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMyCourseLoad(
    val `data`: LoadData,
    val message: String,
    val status: Int,
    val success: Boolean
)

@Serializable
data class LoadUser(
    val machineId: String
)

@Serializable
data class LoadDeparture(
    val city: String,
    val name: String,
    val region: String,
    val town: String
)

@Serializable
data class PrivateCourse(
    val createdAt: String,
    val departure: LoadDeparture,
    val distance: Double,
    val id: Int,
    val image: String
)

@Serializable
data class LoadData(
    val privateCourses: List<PrivateCourse>,
    val user: LoadUser
)