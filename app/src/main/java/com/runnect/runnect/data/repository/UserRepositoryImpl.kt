package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitle
import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.data.dto.response.ResponseDeleteHistory
import com.runnect.runnect.data.dto.response.ResponseDeleteUploadCourse
import com.runnect.runnect.data.dto.response.ResponseDeleteUser
import com.runnect.runnect.data.dto.response.ResponsePatchHistoryTitle
import com.runnect.runnect.data.dto.response.ResponsePatchUserNickName
import com.runnect.runnect.data.dto.response.ResponseGetUser
import com.runnect.runnect.data.source.remote.RemoteUserDataSource
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.util.extension.toData
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val remoteUserDataSource: RemoteUserDataSource) :
    UserRepository {
    override suspend fun getUserInfo(): ResponseGetUser = remoteUserDataSource.getUserInfo()
    override suspend fun updateNickName(requestPatchNickName: RequestPatchNickName): ResponsePatchUserNickName =
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
    ): Result<ResponseDeleteUploadCourse?> = runCatching {
        remoteUserDataSource.putDeleteUploadCourse(requestDeleteUploadCourse).data
    }

    override suspend fun putDeleteHistory(
        requestDeleteHistory: RequestDeleteHistory
    ): Result<ResponseDeleteHistory?> = runCatching {
        remoteUserDataSource.putDeleteHistory(requestDeleteHistory).data
    }

    override suspend fun patchHistoryTitle(
        historyId: Int, requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): Result<ResponsePatchHistoryTitle?> = runCatching {
        remoteUserDataSource.patchHistoryTitle(historyId, requestPatchHistoryTitle).data
    }

    override suspend fun deleteUser(): ResponseDeleteUser {
        return remoteUserDataSource.deleteUser()
    }
}