package com.runnect.runnect.data.api


import com.runnect.runnect.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface KLoginService {
    //로그인
    @POST("/api/auth")
    suspend fun postLogin(
        @Body request: RequestPostLoginDto
    ): Response<ResponsePostLoginDto>

}