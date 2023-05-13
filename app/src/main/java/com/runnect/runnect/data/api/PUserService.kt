package com.runnect.runnect.data.api

import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestEditHistoryTitle
import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.*
import retrofit2.http.*

interface PUserService {
    @GET("api/user")
    suspend fun getUserInfo(
    ): ResponseUser

    @PATCH("api/user")
    suspend fun updateNickName(
        @Body requestUpdateNickName: RequestUpdateNickName,
    ): ResponseUpdateNickName

    @GET("api/stamp/user")
    suspend fun getMyStamp(
    ): ResponseMyStamp

    @GET("api/record/user")
    suspend fun getRecord(
    ): ResponseRecordInfo

    @GET("api/public-course/user")
    suspend fun getUserUploadCourse(
    ): ResponseUserUploadCourse

    @PUT("api/record")
    suspend fun putDeleteHistory(
        @Body requestDeleteHistory: RequestDeleteHistory
    ): ResponseDeleteHistory


    @PUT("api/public-course")
    suspend fun putDeleteUploadCourse(
        @Body requestDeleteUploadCourse: RequestDeleteUploadCourse
    ): ResponseDeleteUploadCourse

    @PATCH("api/record/{recordId}")
    suspend fun patchHistoryTitle(
        @Path("recordId") historyId: Int,
        @Body requestEditHistoryTitle: RequestEditHistoryTitle
    ): ResponseEditHistoryTitle
}