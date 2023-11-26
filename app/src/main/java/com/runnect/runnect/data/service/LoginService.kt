package com.runnect.runnect.data.service


import com.runnect.runnect.data.dto.request.RequestPostLogin
import com.runnect.runnect.data.dto.response.ResponsePostLogin
import com.runnect.runnect.data.dto.response.ResponseGetRefreshToken
import retrofit2.http.*

interface LoginService {
    //로그인
    @POST("/api/auth")
    suspend fun postLogin(
        @Body request: RequestPostLogin
    ): ResponsePostLogin

    //토큰 재발급
    @GET("/api/auth/getNewToken")
    suspend fun getNewToken(

    ): ResponseGetRefreshToken
}