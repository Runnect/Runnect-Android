package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.api.PUserService
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestEditHistoryTitle
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.*

class UserDataSource(private val userService: PUserService) {
    suspend fun getUserInfo(): ResponseUser = userService.getUserInfo()
    suspend fun updateNickName(requestUpdateNickName: RequestUpdateNickName): ResponseUpdateNickName =
        userService.updateNickName(requestUpdateNickName)

    suspend fun getMyStamp(): ResponseMyStamp = userService.getMyStamp()
    suspend fun getRecord(): ResponseRecordInfo = userService.getRecord()
    suspend fun getUserUploadCourse(): ResponseUserUploadCourse = userService.getUserUploadCourse()
    suspend fun putDeleteHistory(requestDeleteHistory: RequestDeleteHistory): ResponseDeleteHistory =
        userService.putDeleteHistory(requestDeleteHistory)
    suspend fun putDeleteUploadCourse(requestDeleteUploadCourse: RequestDeleteUploadCourse): ResponseDeleteUploadCourse =
        userService.putDeleteUploadCourse(requestDeleteUploadCourse)
    suspend fun patchHistoryTitle(historyId:Int, requestEditHistoryTitle: RequestEditHistoryTitle): ResponseEditHistoryTitle =
        userService.patchHistoryTitle(historyId, requestEditHistoryTitle)
}