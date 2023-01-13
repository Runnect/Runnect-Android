package com.runnect.runnect.data.dto.response
import kotlinx.serialization.Serializable

@Serializable
data class ResponseCourseScrap(
    val message: String,
    val status: Int,
    val success: Boolean
)