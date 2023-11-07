package com.runnect.runnect.data.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RequestUpdatePublicCourse(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
)