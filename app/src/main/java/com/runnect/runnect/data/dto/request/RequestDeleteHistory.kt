package com.runnect.runnect.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestDeleteHistory(
    @SerialName("recordIdList")
    val recordIdList: List<Int>
)
