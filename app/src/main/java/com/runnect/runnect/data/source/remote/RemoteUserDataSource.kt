package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.service.UserService
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitle
import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.data.dto.response.*
import com.runnect.runnect.data.dto.response.base.BaseResponse
import javax.inject.Inject

class RemoteUserDataSource @Inject constructor(private val userService: UserService) {
    suspend fun getUserInfo(): ResponseGetUser = userService.getUserInfo()
    suspend fun updateNickName(requestPatchNickName: RequestPatchNickName): ResponsePatchUserNickName =
        userService.updateNickName(requestPatchNickName)

    suspend fun getMyStamp(): ResponseGetMyStamp = userService.getMyStamp()
    suspend fun getRecord(): ResponseGetMyHistory = userService.getRecord()
    suspend fun getUserUploadCourse(): ResponseGetUserUploadCourse = userService.getUserUploadCourse()

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