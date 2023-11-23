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
    suspend fun getUserInfo(): ResponseUser = userService.getUserInfo()
    suspend fun updateNickName(requestPatchNickName: RequestPatchNickName): ResponseUpdateNickName =
        userService.updateNickName(requestPatchNickName)

    suspend fun getMyStamp(): ResponseMyStamp = userService.getMyStamp()
    suspend fun getRecord(): ResponseRecordInfo = userService.getRecord()
    suspend fun getUserUploadCourse(): ResponseUserUploadCourse = userService.getUserUploadCourse()

    suspend fun putDeleteUploadCourse(
        requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): BaseResponse<ResponseDeleteUploadCourseDto> =
        userService.putDeleteUploadCourse(requestDeleteUploadCourse)

    suspend fun putDeleteHistory(requestDeleteHistory: RequestDeleteHistory): BaseResponse<ResponseDeleteHistoryDto> =
        userService.putDeleteHistory(requestDeleteHistory)

    suspend fun patchHistoryTitle(
        historyId: Int,
        requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): BaseResponse<ResponsePatchHistoryTitleDto> =
        userService.patchHistoryTitle(historyId, requestPatchHistoryTitle)

    suspend fun deleteUser(): ResponseDeleteUser = userService.deleteUser()
}