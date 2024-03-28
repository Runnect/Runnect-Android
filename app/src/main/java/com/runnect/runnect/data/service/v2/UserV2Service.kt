package com.runnect.runnect.data.service.v2

import com.runnect.runnect.data.dto.response.ResponseGetUser
import com.runnect.runnect.data.dto.response.ResponseGetUserUploadCourse
import retrofit2.http.GET

interface UserV2Service {

    @GET("api/user")
    suspend fun getUserInfo(): Result<ResponseGetUser>

    @GET("api/public-course/user")
    suspend fun getUserUploadCourse(): Result<ResponseGetUserUploadCourse>
}