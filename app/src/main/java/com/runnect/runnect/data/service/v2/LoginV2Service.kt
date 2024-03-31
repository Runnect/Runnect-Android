package com.runnect.runnect.data.service.v2

import com.runnect.runnect.data.dto.request.RequestPostLogin
import com.runnect.runnect.data.dto.response.ResponsePostLogin
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginV2Service {

    //로그인
    @POST("/api/auth")
    suspend fun postLogin(
        @Body request: RequestPostLogin
    ): Result<ResponsePostLogin>
}