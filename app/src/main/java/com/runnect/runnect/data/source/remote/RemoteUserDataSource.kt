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
import javax.inject.Inject

class RemoteUserDataSource @Inject constructor(private val userService: UserService) {
    suspend fun getUserInfo(): ResponseGetUser = userService.getUserInfo()
    suspend fun updateNickName(requestPatchNickName: RequestPatchNickName): ResponsePatchUserNickName =
        userService.updateNickName(requestPatchNickName)

    suspend fun getMyStamp(): ResponseGetMyStamp = userService.getMyStamp()
    suspend fun getRecord(): ResponseGetMyHistory = userService.getRecord()
    suspend fun getUserUploadCourse(): ResponseGetUserUploadCourse =
        userService.getUserUploadCourse()

    suspend fun getUserProfile(userId: Int): BaseResponse<ResponseGetUserProfile> =
        userService.getUserProfile(userId)

    suspend fun putDeleteUploadCourse(
        requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): BaseResponse<ResponseDeleteUploadCourse> =
        userService.putDeleteUploadCourse(requestDeleteUploadCourse)

    suspend fun putDeleteHistory(requestDeleteHistory: RequestDeleteHistory): BaseResponse<ResponseDeleteHistory> =
        userService.putDeleteHistory(requestDeleteHistory)

    suspend fun patchHistoryTitle(
        historyId: Int,
        requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): BaseResponse<ResponsePatchHistoryTitle> =
        userService.patchHistoryTitle(historyId, requestPatchHistoryTitle)

    suspend fun deleteUser(): ResponseDeleteUser = userService.deleteUser()
}