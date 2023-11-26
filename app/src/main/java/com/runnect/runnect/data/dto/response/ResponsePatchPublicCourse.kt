package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePatchPublicCourse(
    @SerializedName("publicCourse")
    val publicCourse: PublicCourse
)

@Serializable
data class PublicCourse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String
)
