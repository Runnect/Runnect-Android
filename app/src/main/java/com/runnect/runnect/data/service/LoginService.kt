package com.runnect.runnect.data.service


import com.runnect.runnect.data.dto.request.RequestLogin
import com.runnect.runnect.data.dto.response.ResponseLogin
import com.runnect.runnect.data.dto.response.ResponseRefreshToken
import com.runnect.runnect.data.model.*
import retrofit2.http.*

interface LoginService {
    //로그인
    @POST("/api/auth")
    suspend fun postLogin(
        @Body request: RequestLogin
    ): ResponseLogin

    //토큰 재발급
    @GET("/api/auth/getNewToken")
    suspend fun getNewToken(

    ): ResponseRefreshToken
}