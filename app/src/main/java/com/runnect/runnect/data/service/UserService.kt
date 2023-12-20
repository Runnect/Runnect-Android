package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitle
import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.data.dto.response.*
import com.runnect.runnect.data.dto.response.base.BaseResponse
import retrofit2.http.*

interface UserService {
    @GET("api/user")
    suspend fun getUserInfo(
    ): ResponseGetUser

    @PATCH("api/user")
    suspend fun updateNickName(
        @Body requestPatchNickName: RequestPatchNickName,
    ): ResponsePatchUserNickName

    @GET("api/stamp/user")
    suspend fun getMyStamp(
    ): ResponseGetMyStamp

    @GET("api/record/user")
    suspend fun getRecord(
    ): ResponseGetMyHistory

    @GET("api/public-course/user")
    suspend fun getUserUploadCourse(
    ): ResponseGetUserUploadCourse

    @PUT("api/public-course")
    suspend fun putDeleteUploadCourse(
        @Body requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): BaseResponse<ResponseDeleteUploadCourse>

    @PUT("api/record")
    suspend fun putDeleteHistory(
        @Body requestDeleteHistory: RequestDeleteHistory
    ): BaseResponse<ResponseDeleteHistory>

    @PATCH("api/record/{recordId}")
    suspend fun patchHistoryTitle(
        @Path("recordId") historyId: Int,
        @Body requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): BaseResponse<ResponsePatchHistoryTitle>

    @DELETE("api/user")
    suspend fun deleteUser(): ResponseDeleteUser
}