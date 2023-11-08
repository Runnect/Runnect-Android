package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistoryDto
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourseDto
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitleDto
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.*

interface UserRepository {
    suspend fun getUserInfo(): ResponseUser

    suspend fun updateNickName(requestUpdateNickName: RequestUpdateNickName): ResponseUpdateNickName

    suspend fun getMyStamp(): MutableList<String>

    suspend fun getRecord(): MutableList<HistoryInfoDTO>

    suspend fun getUserUploadCourse(): MutableList<UserUploadCourseDTO>

    suspend fun putDeleteUploadCourse(
        requestDeleteUploadCourseDto: RequestDeleteUploadCourseDto
    ): Result<ResponseDeleteUploadCourseDto?>

    suspend fun putDeleteHistory(requestDeleteHistoryDto: RequestDeleteHistoryDto): Result<ResponseDeleteHistoryDto?>

    suspend fun patchHistoryTitle(
        historyId: Int,
        requestPatchHistoryTitleDto: RequestPatchHistoryTitleDto
    ): Result<ResponsePatchHistoryTitleDto?>

    suspend fun deleteUser(): ResponseDeleteUser
}