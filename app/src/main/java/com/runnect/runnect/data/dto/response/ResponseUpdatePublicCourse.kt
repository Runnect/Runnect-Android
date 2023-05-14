package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseUpdatePublicCourse(
    @SerializedName("data")
    val `data`: UpdatePublicCourseData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

@Serializable
data class PublicCourse(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String
)

@Serializable
data class UpdatePublicCourseData(
    @SerializedName("publicCourse")
    val publicCourse: PublicCourse
)