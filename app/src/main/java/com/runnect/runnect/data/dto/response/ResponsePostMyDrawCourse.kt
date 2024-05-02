package com.runnect.runnect.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePostMyDrawCourse(
    @SerialName("id")
    val id: Int,
    @SerialName("createdAt")
    val createdAt: String
)