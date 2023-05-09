package com.runnect.runnect.data.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class RequestEditHistoryTitle(
    @SerializedName("title")
    val title: String
)