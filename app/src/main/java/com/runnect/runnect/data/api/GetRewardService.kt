package com.runnect.runnect.data.api

import com.runnect.runnect.data.model.GetRewardDto
import retrofit2.Response
import retrofit2.http.GET

interface GetRewardService {
    @GET("d72d7cad-92e4-43ab-a5b8-0a3ea936f400")
    suspend fun getRewardList(
    ): Response<GetRewardDto>
}