package com.runnect.runnect.data.api


import com.runnect.runnect.data.dto.request.RequestLogin
import com.runnect.runnect.data.dto.response.ResponseLogin
import com.runnect.runnect.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface LoginService {
    //로그인
    @POST("/api/auth")
    suspend fun postLogin(
        @Body request: RequestLogin
    ): ResponseLogin
}