package com.runnect.runnect.data.dto.request
import kotlinx.serialization.Serializable

@Serializable
data class RequestPostPublicCourse(
    val courseId: Int,
    val description: String,
    val title: String
)