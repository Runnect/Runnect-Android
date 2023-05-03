package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseDeleteHistory(
    @SerializedName("data")
    val `data`: DeleteHistoryData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

@Serializable
data class DeleteHistoryData(
    val deletedRecordIdCount: Int
)