package com.runnect.runnect.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestUpdateNickName(
    val nickname: String
)