package com.runnect.runnect.domain.repository

import com.runnect.runnect.domain.entity.MarkerQuota
import com.runnect.runnect.domain.entity.MarkerRewardResult
import kotlinx.coroutines.flow.Flow

interface MarkerQuotaRepository {

    suspend fun getMarkerQuota(): Flow<Result<MarkerQuota>>

    suspend fun consumeMarkerQuota(amount: Int): Flow<Result<MarkerQuota>>

    suspend fun grantMarkerReward(amount: Int, rewardTransactionId: String): Flow<Result<MarkerRewardResult>>
}
