package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseEditHistoryTitle(
    @SerializedName("data")
    val `data`: DataEditHistoryTitle,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

@Serializable
data class DataEditHistoryTitle(
    @SerializedName("record")
    val record: RecordEditHistoryTitle
)

@Serializable
data class RecordEditHistoryTitle(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String
)