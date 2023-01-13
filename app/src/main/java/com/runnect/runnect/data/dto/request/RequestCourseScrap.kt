package com.runnect.runnect.data.dto.request
import kotlinx.serialization.Serializable

@Serializable
data class RequestCourseScrap(
    val publicCourseId: Int,
    val scrapTF: String
)