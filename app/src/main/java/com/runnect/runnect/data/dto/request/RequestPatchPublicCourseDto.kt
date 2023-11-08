package com.runnect.runnect.data.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPatchPublicCourseDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
)