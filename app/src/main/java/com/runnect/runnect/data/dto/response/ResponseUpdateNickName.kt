package com.runnect.runnect.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseUpdateNickName(
    val `data`: Data,
    val message: String,
    val status: Int,
    val success: Boolean
)

@Serializable
data class UpdateUser(
    val latestStamp: String,
    val level: Int,
    val levelPercent: Int,
    val machineId: String,
    val modifiedAt: String,
    val nickname: String
)

@Serializable
data class UpdateData(
    val user: User
)