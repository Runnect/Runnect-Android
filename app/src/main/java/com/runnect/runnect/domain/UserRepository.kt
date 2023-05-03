package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.ResponseDeleteHistory
import com.runnect.runnect.data.dto.response.ResponseUpdateNickName
import com.runnect.runnect.data.dto.response.ResponseUser

interface UserRepository {
    suspend fun getUserInfo(): ResponseUser
    suspend fun updateNickName(requestUpdateNickName: RequestUpdateNickName): ResponseUpdateNickName
    suspend fun getMyStamp(): MutableList<String>
    suspend fun getRecord(): MutableList<HistoryInfoDTO>
    suspend fun getUserUploadCourse(): MutableList<UserUploadCourseDTO>
    suspend fun putDeleteHistory(requestDeleteHistory: RequestDeleteHistory): ResponseDeleteHistory
}