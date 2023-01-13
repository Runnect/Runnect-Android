package com.runnect.runnect.data.dto.response
import kotlinx.serialization.Serializable

@Serializable
data class ResponseCourseDetail(
    val `data`: DetailData,
    val message: String,
    val status: Int,
    val success: Boolean
)
@Serializable
data class DetailData(
    val publicCourse: DetailPublicCourse,
    val user: CourseDetailUser
)

@Serializable
data class CourseDetailUser(
    val image: String,
    val level: Int,
    val nickname: String
)

@Serializable
data class DetailPublicCourse(
    val courseId: Int,
    val departure: DetailDeparture,
    val description: String,
    val distance: Double,
    val id: Int,
    val image: String,
    val scrap: Boolean,
    val title: String
)

@Serializable
data class DetailDeparture(
    val city: String,
    val name: String?,
    val region: String,
    val town: String
)

