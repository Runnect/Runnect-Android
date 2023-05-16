package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserDeleteData(
    @SerializedName("deletedUserId")
    val deletedUserId: Int
)

@Serializable
data class ResponseDeleteUser(
    @SerializedName("data")
    val `data`: UserDeleteData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)