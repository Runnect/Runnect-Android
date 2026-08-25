package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.MarkerQuota
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMarkerQuota(
    @SerialName("balance")
    val balance: Int
) {
    fun toMarkerQuota(): MarkerQuota {
        return MarkerQuota(balance = balance)
    }
}
