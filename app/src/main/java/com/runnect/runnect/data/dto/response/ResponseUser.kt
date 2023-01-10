package com.runnect.runnect.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseUser(
    val data: Data,
    val message: String,
    val status: Int,
    val success: Boolean
)

@Serializable
data class Data(
    val user: User
)

@Serializable
data class User(
    val latestStamp: String,
    val level: Int,
    val levelPercent: Int,
    val machineId: String,
    val nickname: String
)