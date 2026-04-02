package com.runnect.runnect.developer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseServerStatus(
    @SerialName("status")
    val status: String
)