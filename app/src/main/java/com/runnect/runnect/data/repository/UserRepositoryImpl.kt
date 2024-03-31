package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.domain.entity.UserProfile
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitle
import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.data.dto.response.ResponseDeleteHistory
import com.runnect.runnect.data.dto.response.ResponseDeleteUploadCourse
import com.runnect.runnect.data.dto.response.ResponseDeleteUser
import com.runnect.runnect.data.dto.response.ResponsePatchHistoryTitle
import com.runnect.runnect.data.dto.response.ResponsePatchUserNickName
import com.runnect.runnect.data.network.mapToFlowResult
import com.runnect.runnect.data.source.remote.RemoteUserDataSource
import com.runnect.runnect.domain.entity.User
import com.runnect.runnect.domain.entity.UserUploadCourse
import com.runnect.runnect.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val remoteUserDataSource: RemoteUserDataSource) :
    UserRepository {

    override suspend fun getUserInfo(): Flow<Result<User>> = remoteUserDataSource.getUserInfo()
        .mapToFlowResult { it.toUser() }

    override suspend fun getUserUploadCourse(): Flow<Result<List<UserUploadCourse>>> {
        return remoteUserDataSource.getUserUploadCourse().mapToFlowResult {
            it.toUserUploadCourse()
        }
    }

    override suspend fun putDeleteUploadCourse(
        requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): Flow<Result<ResponseDeleteUploadCourse>>  {
        return remoteUserDataSource.putDeleteUploadCourse(requestDeleteUploadCourse).mapToFlowResult { it }
    }

    override suspend fun putDeleteHistory(
        requestDeleteHistory: RequestDeleteHistory
    ): Flow<Result<ResponseDeleteHistory>> {
        return remoteUserDataSource.putDeleteHistory(requestDeleteHistory).mapToFlowResult { it }
    }

    override suspend fun deleteUser(): Flow<Result<ResponseDeleteUser>> {
        return remoteUserDataSource.deleteUser().mapToFlowResult { it }
    }

    override suspend fun updateNickName(requestPatchNickName: RequestPatchNickName): ResponsePatchUserNickName =
        remoteUserDataSource.updateNickName(requestPatchNickName)

    override suspend fun getRecord(): Flow<Result<List<HistoryInfoDTO>>> {
        return remoteUserDataSource.getRecord().mapToFlowResult {
            it.toHistoryInfoList()
        }
    }

    override suspend fun getMyStamp(): Flow<Result<List<String>>> {
        return remoteUserDataSource.getMyStamp().mapToFlowResult {
            it.toStampList()
        }
    }

    override suspend fun getUserProfile(userId: Int): Result<UserProfile?> =
        runCatching {
            remoteUserDataSource.getUserProfile(userId).data?.toUserProfile()
        }

    override suspend fun patchHistoryTitle(
        historyId: Int, requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): Result<ResponsePatchHistoryTitle?> = runCatching {
        remoteUserDataSource.patchHistoryTitle(historyId, requestPatchHistoryTitle).data
    }
}