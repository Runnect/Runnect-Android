package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.api.PUserService
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.*

class UserDataSource(private val userService: PUserService) {
    suspend fun getUserInfo(): ResponseUser = userService.getUserInfo()
    suspend fun updateNickName(requestUpdateNickName: RequestUpdateNickName): ResponseUpdateNickName =
        userService.updateNickName(requestUpdateNickName)

    suspend fun getMyStamp(): ResponseMyStamp = userService.getMyStamp()
    suspend fun getRecord(): ResponseRecordInfo = userService.getRecord()
    suspend fun getUserUploadCourse():ResponseUserUploadCourse = userService.getUserUploadCourse()
}