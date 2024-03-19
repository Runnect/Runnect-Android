package com.runnect.runnect.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPatchMyDrawCourseTitle(
    @SerialName("title")
    val title: String
)
