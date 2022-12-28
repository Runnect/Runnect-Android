package com.example.runnect.data.api

import com.example.runnect.data.model.GetRewardDto
import retrofit2.Response
import retrofit2.http.GET

interface GetRewardService {
    @GET("8430d64f-841d-40e6-a92e-55123aab5dd5")
    suspend fun getRewardList(
    ): Response<GetRewardDto>
}