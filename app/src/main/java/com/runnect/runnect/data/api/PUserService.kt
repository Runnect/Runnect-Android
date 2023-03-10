package com.runnect.runnect.data.api

import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface PUserService {
    @GET("api/user")
    suspend fun getUserInfo(
    ): ResponseUser

    @PATCH("api/user")
    suspend fun updateNickName(
        @Body requestUpdateNickName: RequestUpdateNickName,
    ): ResponseUpdateNickName

    @GET("api/stamp/user")
    suspend fun getMyStamp(
    ): ResponseMyStamp

    @GET("api/record/user")
    suspend fun getRecord(
    ): ResponseRecordInfo

    @GET("api/public-course/user")
    suspend fun getUserUploadCourse(
    ): ResponseUserUploadCourse
}