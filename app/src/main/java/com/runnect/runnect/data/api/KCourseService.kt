package com.runnect.runnect.data.api


import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.response.RequestPostRecordDto
import com.runnect.runnect.data.dto.response.RequestPutMyDrawDto
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.response.ResponseGetCourseDto
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetailDto
import com.runnect.runnect.data.dto.response.ResponseGetScrapDto
import com.runnect.runnect.data.dto.response.ResponsePostCourseDto
import com.runnect.runnect.data.dto.response.ResponsePostRecordDto
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface KCourseService {
    //스크랩
    @POST("/api/scrap")
    suspend fun postCourseScrap(
        @Body requestCourseScrap: RequestCourseScrap,
    ): Response<ResponseCourseScrap>

    // {id}와 같이 동적인 경로 변수가 없다면 @Path 생략 가능
    //내가 그린 코스 수정
    @PUT("/api/course")
    suspend fun deleteMyDrawCourse(
        @Body deleteCourseList: RequestPutMyDrawDto
    ): Response<ResponsePutMyDrawDto>

    //보관함 내가 그린 코스 가져오기
    @GET("/api/course/user")
    suspend fun getCourseList(
    ): Response<ResponseGetCourseDto>

    //보관함 스크랩 코스 가져오기
    @GET("/api/scrap/user")
    suspend fun getScrapList(
    ): Response<ResponseGetScrapDto>

    //내가 그린 코스 Detail 가져오기
    @GET("/api/course/detail/{courseId}") //이해가 안 되네. :courseId 하면 안 되고 왜 {}하면 되는거지?
    suspend fun getMyDrawDetail(
        @Path("courseId") courseId: Int,
    ): Response<ResponseGetMyDrawDetailDto>

    //기록 업로드
    @POST("/api/record")
    suspend fun postRecord(
        @Body request: RequestPostRecordDto
    ): Response<ResponsePostRecordDto>

    //코스 업로드
    @Multipart
    @POST("/api/course")
    suspend fun uploadCourse(
        @Part image: MultipartBody.Part,
        @Part("data") data: RequestBody,
    ): Response<ResponsePostCourseDto>
}