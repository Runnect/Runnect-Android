package com.runnect.runnect.data.service.v2

import com.runnect.runnect.data.dto.response.ResponseGetUser
import retrofit2.http.GET

interface UserV2Service {

    @GET("api/user")
    suspend fun getUserInfo(): Result<ResponseGetUser>
}