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
    ): ResponseUser

    @PATCH("api/user")
    suspend fun updateNickName(
        @Body requestPatchNickName: RequestPatchNickName,
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

    @PUT("api/public-course")
    suspend fun putDeleteUploadCourse(
        @Body requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): BaseResponse<ResponseDeleteUploadCourseDto>

    @PUT("api/record")
    suspend fun putDeleteHistory(
        @Body requestDeleteHistory: RequestDeleteHistory
    ): BaseResponse<ResponseDeleteHistoryDto>

    @PATCH("api/record/{recordId}")
    suspend fun patchHistoryTitle(
        @Path("recordId") historyId: Int,
        @Body requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): BaseResponse<ResponsePatchHistoryTitleDto>

    @DELETE("api/user")
    suspend fun deleteUser(): ResponseDeleteUser
}