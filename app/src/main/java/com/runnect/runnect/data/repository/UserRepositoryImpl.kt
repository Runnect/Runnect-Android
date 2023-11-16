package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistoryDto
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourseDto
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitleDto
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.ResponseDeleteHistoryDto
import com.runnect.runnect.data.dto.response.ResponseDeleteUploadCourseDto
import com.runnect.runnect.data.dto.response.ResponseDeleteUser
import com.runnect.runnect.data.dto.response.ResponsePatchHistoryTitleDto
import com.runnect.runnect.data.dto.response.ResponseUpdateNickName
import com.runnect.runnect.data.dto.response.ResponseUser
import com.runnect.runnect.data.source.remote.RemoteUserDataSource
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.util.extension.toData
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val remoteUserDataSource: RemoteUserDataSource) :
    UserRepository {
    override suspend fun getUserInfo(): ResponseUser = remoteUserDataSource.getUserInfo()
    override suspend fun updateNickName(requestUpdateNickName: RequestUpdateNickName): ResponseUpdateNickName =
        remoteUserDataSource.updateNickName(requestUpdateNickName)

    override suspend fun getMyStamp(): MutableList<String> {
        return remoteUserDataSource.getMyStamp().data.stamps.map { it.id }.toMutableList()
    }

    override suspend fun getRecord(): MutableList<HistoryInfoDTO> {
        return remoteUserDataSource.getRecord().data.records.map { it.toData() }.toMutableList()
    }

    override suspend fun getUserUploadCourse(): MutableList<UserUploadCourseDTO> {
        return remoteUserDataSource.getUserUploadCourse().data.publicCourses.map { it.toData() }
            .toMutableList()
    }

    override suspend fun putDeleteUploadCourse(
        requestDeleteUploadCourseDto: RequestDeleteUploadCourseDto
    ): Result<ResponseDeleteUploadCourseDto?> = runCatching {
        remoteUserDataSource.putDeleteUploadCourse(requestDeleteUploadCourseDto).data
    }

    override suspend fun putDeleteHistory(
        requestDeleteHistoryDto: RequestDeleteHistoryDto
    ): Result<ResponseDeleteHistoryDto?> = runCatching {
        remoteUserDataSource.putDeleteHistory(requestDeleteHistoryDto).data
    }

    override suspend fun patchHistoryTitle(
        historyId: Int, requestPatchHistoryTitleDto: RequestPatchHistoryTitleDto
    ): Result<ResponsePatchHistoryTitleDto?> = runCatching {
        remoteUserDataSource.patchHistoryTitle(historyId, requestPatchHistoryTitleDto).data
    }

    override suspend fun deleteUser(): ResponseDeleteUser {
        return remoteUserDataSource.deleteUser()
    }
}