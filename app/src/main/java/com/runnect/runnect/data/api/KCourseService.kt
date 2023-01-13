package com.runnect.runnect.data.api


import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.ResponsePostCourseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface KCourseService {
    //코스 가져오기
    @GET("/api/course/user")
    suspend fun getCourseList(
    ): Response<ResponseGetCourseDto>

    //코스 가져오기
    @POST("/api/record")
    suspend fun postRecord(
    ): Response<ResponseGetCourseDto>

    //코스 업로드
    @Multipart
    @POST("/api/course")
    suspend fun uploadCourse(
        @Part image: MultipartBody.Part,
        @Part("data") data: RequestBody, //어노테이션 안에 넣어주는 게 무슨 이름인지 모르겠음 일단 맞춰줬음
    ): Response<ResponsePostCourseDto>
}