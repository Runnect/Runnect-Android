package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePatchUserNickName(
    @SerializedName("data")
    val `data`: UpdateData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

@Serializable
data class UpdateUser(
    @SerializedName("latestStamp")
    val latestStamp: String,
    @SerializedName("level")
    val level: Int,
    @SerializedName("levelPercent")
    val levelPercent: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("modifiedAt")
    val modifiedAt: String,
    @SerializedName("nickname")
    val nickname: String
)

@Serializable
data class UpdateData(
    @SerializedName("user")
    val user: UpdateUser
)