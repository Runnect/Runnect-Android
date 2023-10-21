package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistoryDto
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestEditHistoryTitle
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.ResponseDeleteHistoryDto
import com.runnect.runnect.data.dto.response.ResponseDeleteUploadCourse
import com.runnect.runnect.data.dto.response.ResponseDeleteUser
import com.runnect.runnect.data.dto.response.ResponseEditHistoryTitle
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

    override suspend fun putDeleteUploadCourse(requestDeleteUploadCourse: RequestDeleteUploadCourse): ResponseDeleteUploadCourse {
        return remoteUserDataSource.putDeleteUploadCourse(requestDeleteUploadCourse)
    }

    override suspend fun putDeleteHistory(requestDeleteHistoryDto: RequestDeleteHistoryDto): Result<ResponseDeleteHistoryDto?> =
        runCatching {
            remoteUserDataSource.putDeleteHistory(requestDeleteHistoryDto).data
        }

    override suspend fun patchHistoryTitle(
        historyId: Int, requestEditHistoryTitle: RequestEditHistoryTitle
    ): ResponseEditHistoryTitle {
        return remoteUserDataSource.patchHistoryTitle(historyId, requestEditHistoryTitle)
    }

    override suspend fun deleteUser(): ResponseDeleteUser {
        return remoteUserDataSource.deleteUser()
    }
}