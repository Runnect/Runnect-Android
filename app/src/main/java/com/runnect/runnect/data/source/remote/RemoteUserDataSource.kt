package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitle
import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.data.dto.response.ResponseDeleteHistory
import com.runnect.runnect.data.dto.response.ResponseDeleteUploadCourse
import com.runnect.runnect.data.dto.response.ResponseDeleteUser
import com.runnect.runnect.data.dto.response.ResponseGetMyHistory
import com.runnect.runnect.data.dto.response.ResponseGetMyStamp
import com.runnect.runnect.data.dto.response.ResponseGetUser
import com.runnect.runnect.data.dto.response.ResponseGetUserProfile
import com.runnect.runnect.data.dto.response.ResponseGetUserUploadCourse
import com.runnect.runnect.data.dto.response.ResponsePatchHistoryTitle
import com.runnect.runnect.data.dto.response.ResponsePatchUserNickName
import com.runnect.runnect.data.dto.response.base.BaseResponse
import com.runnect.runnect.data.service.UserService
import com.runnect.runnect.data.service.v2.UserV2Service
import javax.inject.Inject

class RemoteUserDataSource @Inject constructor(
    private val userV2Service: UserV2Service,
    private val userService: UserService
) {
    suspend fun getUserInfo(): Result<ResponseGetUser> = userV2Service.getUserInfo()

    suspend fun getUserUploadCourse(): Result<ResponseGetUserUploadCourse> =
        userV2Service.getUserUploadCourse()

    suspend fun deleteUser(): Result<ResponseDeleteUser> = userV2Service.deleteUser()

    suspend fun getRecord(): Result<ResponseGetMyHistory> = userV2Service.getRecord()

    suspend fun getMyStamp(): Result<ResponseGetMyStamp> = userV2Service.getMyStamp()

    suspend fun putDeleteUploadCourse(
        requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): Result<ResponseDeleteUploadCourse> =
        userV2Service.putDeleteUploadCourse(requestDeleteUploadCourse)

    suspend fun putDeleteHistory(requestDeleteHistory: RequestDeleteHistory): Result<ResponseDeleteHistory> =
        userV2Service.putDeleteHistory(requestDeleteHistory)

    suspend fun patchHistoryTitle(
        historyId: Int,
        requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): Result<ResponsePatchHistoryTitle> =
        userV2Service.patchHistoryTitle(historyId, requestPatchHistoryTitle)

    suspend fun updateNickName(
        requestPatchNickName: RequestPatchNickName
    ): Result<ResponsePatchUserNickName> =
        userV2Service.updateNickName(requestPatchNickName)

    suspend fun getUserProfile(userId: Int): BaseResponse<ResponseGetUserProfile> =
        userService.getUserProfile(userId)
}