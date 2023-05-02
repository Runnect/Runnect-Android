package com.runnect.runnect.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestDeleteHistory(
    val recordIdList: List<Int>
)