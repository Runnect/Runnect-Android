package com.runnect.runnect.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPostLogin(
    @SerialName("token")
    val token: String?,
    @SerialName("provider")
    val provider: String,
)
