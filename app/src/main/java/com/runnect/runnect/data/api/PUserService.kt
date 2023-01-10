package com.runnect.runnect.data.api

import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.ResponseUpdateNickName
import com.runnect.runnect.data.dto.response.ResponseUser
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface PUserService {
    @GET("api/user")
    suspend fun getUserInfo(
    ): ResponseUser

    @PATCH("api/user")
    suspend fun updateNickName(
        @Body requestUpdateNickName: RequestUpdateNickName
    ): ResponseUpdateNickName
}