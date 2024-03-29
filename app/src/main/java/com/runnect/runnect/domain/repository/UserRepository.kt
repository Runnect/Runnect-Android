package com.runnect.runnect.domain.repository

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
import com.runnect.runnect.domain.entity.User
import com.runnect.runnect.domain.entity.UserUploadCourse
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserInfo(): Flow<Result<User>>

    suspend fun getUserUploadCourse(): Flow<Result<List<UserUploadCourse>>>

    suspend fun putDeleteUploadCourse(
        requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): Flow<Result<ResponseDeleteUploadCourse>>

    suspend fun deleteUser(): Flow<Result<ResponseDeleteUser>>

    suspend fun updateNickName(requestPatchNickName: RequestPatchNickName): ResponsePatchUserNickName

    suspend fun getMyStamp(): MutableList<String>

    suspend fun getRecord(): MutableList<HistoryInfoDTO>

    suspend fun getUserProfile(userId: Int): Result<UserProfile?>

    suspend fun putDeleteHistory(requestDeleteHistory: RequestDeleteHistory): Result<ResponseDeleteHistory?>

    suspend fun patchHistoryTitle(
        historyId: Int,
        requestPatchHistoryTitle: RequestPatchHistoryTitle
    ): Result<ResponsePatchHistoryTitle?>
}