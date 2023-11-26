package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseUserUploadCourse(
    @SerializedName("data")
    val `data`: DataUpload,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)
@Serializable
data class UserUpload(
    @SerializedName("id")
    val id: Int
)
@Serializable
data class PublicCourseUpload(
    @SerializedName("courseId")
    val courseId: Int,
    @SerializedName("departure")
    val departure: DepartureUpload,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("title")
    val title: String
)

@Serializable
data class DepartureUpload(
    @SerializedName("city")
    val city: String,
    @SerializedName("region")
    val region: String
)
@Serializable
data class DataUpload(
    @SerializedName("publicCourse")
    val publicCourses: List<PublicCourseUpload>,
    @SerializedName("user")
    val user: UserUpload
)