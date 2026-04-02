package com.runnect.runnect.data.dto.response.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse<T>(
    @SerialName("status")
    val status: Int,
    @SerialName("success")
    val success: Boolean?,
    @SerialName("message")
    val message: String?,
    @SerialName("error")
    val error: String?,
    @SerialName("data")
    val data: T? = null
)
