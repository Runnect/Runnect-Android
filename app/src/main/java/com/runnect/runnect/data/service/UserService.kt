package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.data.dto.response.*
import com.runnect.runnect.data.dto.response.base.BaseResponse
import retrofit2.http.*

interface UserService {

    @PATCH("api/user")
    suspend fun updateNickName(
        @Body requestPatchNickName: RequestPatchNickName,
    ): ResponsePatchUserNickName

    // 유저 프로필 조회
    @GET("/api/user/{profileUserId}")
    suspend fun getUserProfile(
        @Path("profileUserId") userId: Int,
    ): BaseResponse<ResponseGetUserProfile>
}