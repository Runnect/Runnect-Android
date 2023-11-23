package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitle
import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.data.dto.response.*

interface UserRepository {
    suspend fun getUserInfo(): ResponseUser

    suspend fun updateNickName(requestPatchNickName: RequestPatchNickName): ResponseUpdateNickName

    suspend fun getMyStamp(): MutableList<String>

    suspend fun getRecord(): MutableList<HistoryInfoDTO>

    suspend fun getUserUploadCourse(): MutableList<UserUploadCourseDTO>

    suspend fun putDeleteUploadCourse(
        requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): Result<ResponseDeleteUploadCourseDto?>

    suspend fun putDeleteHistory(requestDeleteHistory: RequestDeleteHistory): Result<ResponseDeleteHistoryDto?>

    suspend fun patchHistoryTitle(
        historyId: Int,
        requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): Result<ResponsePatchHistoryTitleDto?>

    suspend fun deleteUser(): ResponseDeleteUser
}