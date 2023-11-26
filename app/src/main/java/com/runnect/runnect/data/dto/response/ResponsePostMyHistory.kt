package com.runnect.runnect.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePostMyHistory(
    @SerialName("data")
    val data: Data,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Int,
    @SerialName("success")
    val success: Boolean,
) {
    @Serializable
    data class Data(
        @SerialName("record")
        val record: Record,
    ) {
        @Serializable
        data class Record(
            @SerialName("createdAt")
            val createdAt: String,
            @SerialName("id")
            val id: Int,
        )
    }
}