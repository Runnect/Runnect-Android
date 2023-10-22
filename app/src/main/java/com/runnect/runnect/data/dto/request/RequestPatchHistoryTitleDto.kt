package com.runnect.runnect.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RequestPatchHistoryTitleDto(
    @SerialName("title")
    val title: String
)
