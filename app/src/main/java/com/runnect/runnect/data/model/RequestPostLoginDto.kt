package com.runnect.runnect.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPostLoginDto(
    @SerialName("token")
    val token: String?,

    @SerialName("provider")
    val provider: String,
)