package com.runnect.runnect.data.api


import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.ResponseGetMyDrawDetailDto
import com.runnect.runnect.data.model.ResponsePostCourseDto
import com.runnect.runnect.data.model.ResponsePostRecordDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface KCourseService {
    //보관함 코스 가져오기
    @GET("/api/course/user")
    suspend fun getCourseList(
    ): Response<ResponseGetCourseDto>

    //내가 그린 코스 Detail 가져오기
    @GET("/api/course/detail/{courseId}") //이해가 안 되네. :courseId 하면 안 되고 왜 {}하면 되는거지?
    suspend fun getMyDrawDetail(
        @Path("courseId") courseId: Int
    ): Response<ResponseGetMyDrawDetailDto>

    //기록 업로드
    @POST("/api/record")
    suspend fun postRecord(
    ): Response<ResponsePostRecordDto>

    //코스 업로드
    @Multipart
    @POST("/api/course")
    suspend fun uploadCourse(
        @Part image: MultipartBody.Part,
        @Part("data") data: RequestBody, //어노테이션 안에 넣어주는 게 무슨 이름인지 모르겠음 일단 맞춰줬음
    ): Response<ResponsePostCourseDto>
}