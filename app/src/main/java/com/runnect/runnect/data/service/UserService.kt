package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitle
import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.data.dto.response.*
import com.runnect.runnect.data.dto.response.base.BaseResponse
import retrofit2.http.*

interface UserService {

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

    @PUT("api/record")
    suspend fun putDeleteHistory(
        @Body requestDeleteHistory: RequestDeleteHistory
    ): BaseResponse<ResponseDeleteHistory>

    @PATCH("api/record/{recordId}")
    suspend fun patchHistoryTitle(
        @Path("recordId") historyId: Int,
        @Body requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): BaseResponse<ResponsePatchHistoryTitle>

    // 유저 프로필 조회
    @GET("/api/user/{profileUserId}")
    suspend fun getUserProfile(
        @Path("profileUserId") userId: Int,
    ): BaseResponse<ResponseGetUserProfile>
}