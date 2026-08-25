package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.MarkerRewardResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGrantMarkerReward(
    @SerialName("granted")
    val granted: Boolean,
    @SerialName("balance")
    val balance: Int
) {
    fun toMarkerRewardResult(): MarkerRewardResult {
        return MarkerRewardResult(granted = granted, balance = balance)
    }
}
