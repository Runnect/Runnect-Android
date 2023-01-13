package com.runnect.runnect.data.dto.response
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMyStamp(
    val `data`: StampData,
    val message: String,
    val status: Int,
    val success: Boolean
)

@Serializable
data class StampData(
    val stamps: List<Stamp>,
    val user: StampUser
)

@Serializable
data class Stamp(
    val id: String
)

@Serializable
data class StampUser(
    val machineId: String
)