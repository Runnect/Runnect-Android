package com.runnect.runnect.data.dto.response
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePostDiscoverUpload(
    val `data`: UploadData,
    val message: String,
    val status: Int,
    val success: Boolean
)

@Serializable
data class UploadPublicCourse(
    val createdAt: String,
    val id: Int
)

@Serializable
data class UploadData(
    val publicCourse: UploadPublicCourse
)