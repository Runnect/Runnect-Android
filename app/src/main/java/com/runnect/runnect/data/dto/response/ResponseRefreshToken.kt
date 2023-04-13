package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseRefreshToken(
    @SerializedName("data")
    val data: Token,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)
@Serializable
data class Token(
    @SerializedName("accessToken")
    val accessToken:String,
    @SerializedName("refreshToken")
    val refreshToken:String
)