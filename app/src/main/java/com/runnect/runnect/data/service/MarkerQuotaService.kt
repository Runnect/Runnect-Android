package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.request.RequestConsumeMarkerQuota
import com.runnect.runnect.data.dto.request.RequestGrantMarkerReward
import com.runnect.runnect.data.dto.response.ResponseGrantMarkerReward
import com.runnect.runnect.data.dto.response.ResponseMarkerQuota
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MarkerQuotaService {

    @GET("/api/marker-quota")
    suspend fun getMarkerQuota(): Result<ResponseMarkerQuota>

    @POST("/api/marker-quota/consume")
    suspend fun consumeMarkerQuota(
        @Body requestConsumeMarkerQuota: RequestConsumeMarkerQuota,
    ): Result<ResponseMarkerQuota>

    @POST("/api/marker-quota/reward")
    suspend fun grantMarkerReward(
        @Body requestGrantMarkerReward: RequestGrantMarkerReward,
    ): Result<ResponseGrantMarkerReward>
}
