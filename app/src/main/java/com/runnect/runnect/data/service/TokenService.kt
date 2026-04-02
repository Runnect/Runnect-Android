package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.response.ResponseGetRefreshToken
import retrofit2.http.*

interface TokenService {

    //토큰 재발급
    @GET("/api/auth/getNewToken")
    suspend fun getNewToken(): ResponseGetRefreshToken
}