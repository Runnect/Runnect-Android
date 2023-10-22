package com.runnect.runnect.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePatchHistoryTitleDto(
    @SerialName("record")
    val record: Record
) {
    @Serializable
    data class Record(
        @SerialName("id")
        val id: Int,
        @SerialName("title")
        val title: String
    )
}
