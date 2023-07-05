package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestEditHistoryTitle
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.*
import com.runnect.runnect.data.source.remote.RemoteUserDataSource
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.util.extension.toData
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val remoteUserDataSource: RemoteUserDataSource) : UserRepository {
    override suspend fun getUserInfo(): ResponseUser = remoteUserDataSource.getUserInfo()
    override suspend fun updateNickName(requestUpdateNickName: RequestUpdateNickName): ResponseUpdateNickName =
        remoteUserDataSource.updateNickName(requestUpdateNickName)

    override suspend fun getMyStamp(): MutableList<String> {
        val stampList = mutableListOf<String>()
        for (i in remoteUserDataSource.getMyStamp().data.stamps) {
            stampList.add(i.id)
        }
        return stampList
    }

    override suspend fun getRecord(): MutableList<HistoryInfoDTO> {
        val recordList = mutableListOf<HistoryInfoDTO>()
        for (i in remoteUserDataSource.getRecord().data.records) {
            recordList.add(i.toData())
        }
        return recordList
    }

    override suspend fun getUserUploadCourse(): MutableList<UserUploadCourseDTO> {
        val userUploadCourseList = mutableListOf<UserUploadCourseDTO>()
        for (i in remoteUserDataSource.getUserUploadCourse().data.publicCourses) {
            userUploadCourseList.add(i.toData())
        }
        return userUploadCourseList
    }

    override suspend fun putDeleteHistory(requestDeleteHistory: RequestDeleteHistory): ResponseDeleteHistory {
        return remoteUserDataSource.putDeleteHistory(requestDeleteHistory)
    }

    override suspend fun putDeleteUploadCourse(requestDeleteUploadCourse: RequestDeleteUploadCourse): ResponseDeleteUploadCourse {
        return remoteUserDataSource.putDeleteUploadCourse(requestDeleteUploadCourse)
    }

    override suspend fun patchHistoryTitle(
        historyId: Int,
        requestEditHistoryTitle: RequestEditHistoryTitle
    ): ResponseEditHistoryTitle {
        return remoteUserDataSource.patchHistoryTitle(historyId, requestEditHistoryTitle)
    }

    override suspend fun deleteUser(): ResponseDeleteUser {
        return remoteUserDataSource.deleteUser()
    }
}