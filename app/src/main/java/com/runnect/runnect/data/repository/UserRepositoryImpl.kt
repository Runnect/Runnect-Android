package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestEditHistoryTitle
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.*
import com.runnect.runnect.data.source.remote.UserDataSource
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.util.extension.toData

class UserRepositoryImpl(private val userDataSource: UserDataSource) : UserRepository {
    override suspend fun getUserInfo(): ResponseUser = userDataSource.getUserInfo()
    override suspend fun updateNickName(requestUpdateNickName: RequestUpdateNickName): ResponseUpdateNickName =
        userDataSource.updateNickName(requestUpdateNickName)

    override suspend fun getMyStamp(): MutableList<String> {
        val stampList = mutableListOf<String>()
        for (i in userDataSource.getMyStamp().data.stamps) {
            stampList.add(i.id)
        }
        return stampList
    }

    override suspend fun getRecord(): MutableList<HistoryInfoDTO> {
        val recordList = mutableListOf<HistoryInfoDTO>()
        for (i in userDataSource.getRecord().data.records) {
            recordList.add(i.toData())
        }
        return recordList
    }

    override suspend fun getUserUploadCourse(): MutableList<UserUploadCourseDTO> {
        val userUploadCourseList = mutableListOf<UserUploadCourseDTO>()
        for (i in userDataSource.getUserUploadCourse().data.publicCourses) {
            userUploadCourseList.add(i.toData())
        }
        return userUploadCourseList
    }

    override suspend fun putDeleteHistory(requestDeleteHistory: RequestDeleteHistory): ResponseDeleteHistory {
        return userDataSource.putDeleteHistory(requestDeleteHistory)
    }

    override suspend fun putDeleteUploadCourse(requestDeleteUploadCourse: RequestDeleteUploadCourse): ResponseDeleteUploadCourse {
        return userDataSource.putDeleteUploadCourse(requestDeleteUploadCourse)
    }

    override suspend fun patchHistoryTitle(
        historyId: Int,
        requestEditHistoryTitle: RequestEditHistoryTitle
    ): ResponseEditHistoryTitle {
        return userDataSource.patchHistoryTitle(historyId, requestEditHistoryTitle)
    }
}