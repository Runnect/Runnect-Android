package com.runnect.runnect.data.dto.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestLogin(
    @SerialName("token")
    val token: String?,

    @SerialName("provider")
    val provider: String,
)