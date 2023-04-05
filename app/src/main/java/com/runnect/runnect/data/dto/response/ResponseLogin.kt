package com.runnect.runnect.data.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseLogin(
    @SerialName("data")
    val `data`: Data,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Int,
    @SerialName("success")
    val success: Boolean,
) {
    @Serializable
    data class Data(
        @SerialName("accessToken")
        val accessToken: String,
        @SerialName("email")
        val email: String,
        @SerialName("refreshToken")
        val refreshToken: String,
        @SerialName("type")
        val type: String,
    )
}