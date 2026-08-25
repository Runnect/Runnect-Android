package com.runnect.runnect.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestGrantMarkerReward(
    @SerialName("amount")
    val amount: Int,
    @SerialName("rewardTransactionId")
    val rewardTransactionId: String
)
