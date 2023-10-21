package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.service.UserService
import com.runnect.runnect.data.dto.request.RequestDeleteHistoryDto
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestEditHistoryTitle
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.*
import com.runnect.runnect.data.dto.response.base.BaseResponse
import javax.inject.Inject

class RemoteUserDataSource @Inject constructor(private val userService: UserService) {
    suspend fun getUserInfo(): ResponseUser = userService.getUserInfo()
    suspend fun updateNickName(requestUpdateNickName: RequestUpdateNickName): ResponseUpdateNickName =
        userService.updateNickName(requestUpdateNickName)

    suspend fun getMyStamp(): ResponseMyStamp = userService.getMyStamp()
    suspend fun getRecord(): ResponseRecordInfo = userService.getRecord()
    suspend fun getUserUploadCourse(): ResponseUserUploadCourse = userService.getUserUploadCourse()

    suspend fun putDeleteUploadCourse(requestDeleteUploadCourse: RequestDeleteUploadCourse): ResponseDeleteUploadCourse =
        userService.putDeleteUploadCourse(requestDeleteUploadCourse)

    suspend fun putDeleteHistory(requestDeleteHistoryDto: RequestDeleteHistoryDto): BaseResponse<ResponseDeleteHistoryDto> =
        userService.putDeleteHistory(requestDeleteHistoryDto)

    suspend fun patchHistoryTitle(
        historyId: Int,
        requestEditHistoryTitle: RequestEditHistoryTitle
    ): ResponseEditHistoryTitle =
        userService.patchHistoryTitle(historyId, requestEditHistoryTitle)

    suspend fun deleteUser(): ResponseDeleteUser = userService.deleteUser()
}