package com.runnect.runnect.data.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePutMyDrawDto(
    @SerialName("data")
    val data: Data,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Int,
    @SerialName("success")
    val success: Boolean
) {
    @Serializable
    data class Data(
        @SerialName("deletedCourseCount")
        val deletedCourseCount: Int
    )
}