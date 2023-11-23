package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitle
import com.runnect.runnect.data.dto.request.RequestPatchNickName
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
    override suspend fun updateNickName(requestPatchNickName: RequestPatchNickName): ResponseUpdateNickName =
        remoteUserDataSource.updateNickName(requestPatchNickName)

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
        requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): Result<ResponseDeleteUploadCourseDto?> = runCatching {
        remoteUserDataSource.putDeleteUploadCourse(requestDeleteUploadCourse).data
    }

    override suspend fun putDeleteHistory(
        requestDeleteHistory: RequestDeleteHistory
    ): Result<ResponseDeleteHistoryDto?> = runCatching {
        remoteUserDataSource.putDeleteHistory(requestDeleteHistory).data
    }

    override suspend fun patchHistoryTitle(
        historyId: Int, requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): Result<ResponsePatchHistoryTitleDto?> = runCatching {
        remoteUserDataSource.patchHistoryTitle(historyId, requestPatchHistoryTitle).data
    }

    override suspend fun deleteUser(): ResponseDeleteUser {
        return remoteUserDataSource.deleteUser()
    }
}