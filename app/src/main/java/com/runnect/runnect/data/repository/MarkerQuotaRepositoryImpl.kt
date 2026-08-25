package com.runnect.runnect.data.repository

import com.runnect.runnect.data.network.mapToFlowResult
import com.runnect.runnect.data.source.remote.RemoteMarkerQuotaDataSource
import com.runnect.runnect.domain.entity.MarkerQuota
import com.runnect.runnect.domain.entity.MarkerRewardResult
import com.runnect.runnect.domain.repository.MarkerQuotaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MarkerQuotaRepositoryImpl @Inject constructor(
    private val remoteMarkerQuotaDataSource: RemoteMarkerQuotaDataSource
) : MarkerQuotaRepository {

    override suspend fun getMarkerQuota(): Flow<Result<MarkerQuota>> =
        remoteMarkerQuotaDataSource.getMarkerQuota().mapToFlowResult {
            it.toMarkerQuota()
        }

    override suspend fun consumeMarkerQuota(amount: Int): Flow<Result<MarkerQuota>> =
        remoteMarkerQuotaDataSource.consumeMarkerQuota(amount = amount).mapToFlowResult {
            it.toMarkerQuota()
        }

    override suspend fun grantMarkerReward(
        amount: Int,
        rewardTransactionId: String
    ): Flow<Result<MarkerRewardResult>> =
        remoteMarkerQuotaDataSource.grantMarkerReward(
            amount = amount,
            rewardTransactionId = rewardTransactionId
        ).mapToFlowResult {
            it.toMarkerRewardResult()
        }
}
