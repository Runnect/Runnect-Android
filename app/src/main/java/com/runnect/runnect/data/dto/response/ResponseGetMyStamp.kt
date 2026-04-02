package com.runnect.runnect.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetMyStamp(
    val user: StampUser,
    val stamps: List<Stamp>,
) {
    @Serializable
    data class StampUser(
        val id: Int
    )

    @Serializable
    data class Stamp(
        val id: String
    )

    fun toStampList() = stamps.map { it.id }
}