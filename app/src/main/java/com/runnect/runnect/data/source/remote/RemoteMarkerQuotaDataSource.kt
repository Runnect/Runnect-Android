package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.dto.request.RequestConsumeMarkerQuota
import com.runnect.runnect.data.dto.request.RequestGrantMarkerReward
import com.runnect.runnect.data.service.MarkerQuotaService
import javax.inject.Inject

class RemoteMarkerQuotaDataSource @Inject constructor(
    private val markerQuotaService: MarkerQuotaService,
) {
    suspend fun getMarkerQuota() = markerQuotaService.getMarkerQuota()

    suspend fun consumeMarkerQuota(amount: Int) =
        markerQuotaService.consumeMarkerQuota(RequestConsumeMarkerQuota(amount = amount))

    suspend fun grantMarkerReward(amount: Int, rewardTransactionId: String) =
        markerQuotaService.grantMarkerReward(
            RequestGrantMarkerReward(amount = amount, rewardTransactionId = rewardTransactionId)
        )
}
