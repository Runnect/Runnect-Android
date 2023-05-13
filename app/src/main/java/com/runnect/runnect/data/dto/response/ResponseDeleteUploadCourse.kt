package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
@Serializable
data class ResponseDeleteUploadCourse(
    @SerializedName("data")
    val `data`: DeleteUploadCourseData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

@Serializable
data class DeleteUploadCourseData(
    @SerializedName("deletedPublicCourseCount")
    val deletedPublicCourseCount: Int
)