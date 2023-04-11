package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseCourseDetail(
    @SerializedName("data")
    val `data`: DetailData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

@Serializable
data class DetailData(
    @SerializedName("publicCourse")
    val publicCourse: DetailPublicCourse,
    @SerializedName("user")
    val user: CourseDetailUser
)

@Serializable
data class CourseDetailUser(
    @SerializedName("image")
    val image: String,
    @SerializedName("isNowUser")
    val isNowUser: Boolean,
    @SerializedName("level")
    val level: Int,
    @SerializedName("nickname")
    val nickname: String
)

@Serializable
data class DetailPublicCourse(
    @SerializedName("courseId")
    val courseId: Int,
    @SerializedName("departure")
    val departure: DetailDeparture,
    @SerializedName("description")
    val description: String,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("path")
    val path: List<List<Double>>,
    @SerializedName("scrap")
    val scrap: Boolean,
    @SerializedName("title")
    val title: String
)

@Serializable
data class DetailDeparture(
    val city: String,
    val name: String,
    val region: String,
    val town: String
)
