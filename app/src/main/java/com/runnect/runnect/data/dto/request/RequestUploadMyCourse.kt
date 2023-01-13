package com.runnect.runnect.data.dto.request
import kotlinx.serialization.Serializable

@Serializable
data class RequestUploadMyCourse(
    val courseId: Int,
    val description: String,
    val title: String
)