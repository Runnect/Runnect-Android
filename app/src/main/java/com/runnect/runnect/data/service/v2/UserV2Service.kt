package com.runnect.runnect.data.service.v2

import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.response.ResponseDeleteHistory
import com.runnect.runnect.data.dto.response.ResponseDeleteUploadCourse
import com.runnect.runnect.data.dto.response.ResponseDeleteUser
import com.runnect.runnect.data.dto.response.ResponseGetMyHistory
import com.runnect.runnect.data.dto.response.ResponseGetMyStamp
import com.runnect.runnect.data.dto.response.ResponseGetUser
import com.runnect.runnect.data.dto.response.ResponseGetUserUploadCourse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserV2Service {

    @GET("api/user")
    suspend fun getUserInfo(): Result<ResponseGetUser>

    @GET("api/public-course/user")
    suspend fun getUserUploadCourse(): Result<ResponseGetUserUploadCourse>

    @GET("api/record/user")
    suspend fun getRecord(): Result<ResponseGetMyHistory>

    @GET("api/stamp/user")
    suspend fun getMyStamp(): Result<ResponseGetMyStamp>

    @PUT("api/public-course")
    suspend fun putDeleteUploadCourse(
        @Body requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): Result<ResponseDeleteUploadCourse>

    @PUT("api/record")
    suspend fun putDeleteHistory(
        @Body requestDeleteHistory: RequestDeleteHistory
    ): Result<ResponseDeleteHistory>

    @DELETE("api/user")
    suspend fun deleteUser(): Result<ResponseDeleteUser>
}