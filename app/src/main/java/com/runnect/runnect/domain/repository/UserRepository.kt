package com.runnect.runnect.domain.repository

import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitle
import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.data.dto.response.*

interface UserRepository {
    suspend fun getUserInfo(): ResponseGetUser

    suspend fun updateNickName(requestPatchNickName: RequestPatchNickName): ResponsePatchUserNickName

    suspend fun getMyStamp(): MutableList<String>

    suspend fun getRecord(): MutableList<HistoryInfoDTO>

    suspend fun getUserUploadCourse(): MutableList<UserUploadCourseDTO>

    suspend fun putDeleteUploadCourse(
        requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): Result<ResponseDeleteUploadCourse?>

    suspend fun putDeleteHistory(requestDeleteHistory: RequestDeleteHistory): Result<ResponseDeleteHistory?>

    suspend fun patchHistoryTitle(
        historyId: Int,
        requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): Result<ResponsePatchHistoryTitle?>

    suspend fun deleteUser(): ResponseDeleteUser
}