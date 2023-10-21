package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseDeleteHistoryDto(
    @SerializedName("deletedRecordIdCount")
    val deletedRecordIdCount: Int
)
