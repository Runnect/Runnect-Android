package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestPostRecordDTO
import com.runnect.runnect.data.dto.request.RequestPutMyDrawDTO
import com.runnect.runnect.data.dto.request.RequestPatchPublicCourseDto
import com.runnect.runnect.data.dto.request.RequestUploadMyCourse
import com.runnect.runnect.data.dto.response.*
import com.runnect.runnect.data.dto.response.base.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface CourseService {
    @GET("/api/public-course")
    suspend fun getRecommendCourse(
        @Query("pageNo") pageNo: String?,
        ): ResponseRecommendCourse

    @POST("/api/scrap")
    suspend fun postCourseScrap(
        @Body requestCourseScrap: RequestCourseScrap,
    ): ResponseCourseScrap

    @GET("/api/public-course/search?")
    suspend fun getCourseSearch(
        @Query("keyword") keyword: String,
    ): ResponseCourseSearch

    @GET("/api/public-course/detail/{publicCourseId}")
    suspend fun getCourseDetail(
        @Path("publicCourseId") publicCourseId: Int,
    ): BaseResponse<ResponseGetCourseDetailDto>

    @GET("/api/course/private/user")
    suspend fun getMyCourseLoad(
    ): ResponseMyCourseLoad

    @POST("/api/public-course")
    suspend fun postUploadMyCourse(
        @Body requestUploadMyCourse: RequestUploadMyCourse,
    ): ResponseUploadMyCourse

    @PATCH("/api/public-course/{publicCourseId}")
    suspend fun patchPublicCourse(
        @Path("publicCourseId") publicCourseId: Int,
        @Body requestPatchPublicCourseDto: RequestPatchPublicCourseDto
    ): BaseResponse<ResponsePatchPublicCourseDto>

    //스크랩 - 지훈이랑 나랑 타입 통일이 안 돼있음. 일단 DataSource 통일부터하고 나중에 여기 타입 맞춰줘서 스크랩 함수 둘 중 하나 삭제해야 함.
    @POST("/api/scrap")
    suspend fun postScrap(
        @Body requestCourseScrap: RequestCourseScrap,
    ): Response<ResponseCourseScrap>

    // {id}와 같이 동적인 경로 변수가 없다면 @Path 생략 가능
    //내가 그린 코스 수정
    @PUT("/api/course")
    suspend fun deleteMyDrawCourse(
        @Body deleteCourseList: RequestPutMyDrawDTO
    ): Response<ResponsePutMyDrawDTO>

    //보관함 내가 그린 코스 가져오기
    @GET("/api/course/user")
    suspend fun getCourseList(
    ): Response<ResponseGetCourseDTO>

    //보관함 스크랩 코스 가져오기
    @GET("/api/scrap/user")
    suspend fun getScrapList(
    ): Response<ResponseGetScrapDTO>

    //내가 그린 코스 Detail 가져오기
    @GET("/api/course/detail/{courseId}")
    suspend fun getMyDrawDetail(
        @Path("courseId") courseId: Int,
    ): Response<ResponseGetMyDrawDetailDTO>

    //기록 업로드
    @POST("/api/record")
    suspend fun postRecord(
        @Body request: RequestPostRecordDTO
    ): Response<ResponsePostRecordDTO>

    //코스 업로드
    @Multipart
    @POST("/api/course/v2")
    suspend fun uploadCourse(
        @Part image: MultipartBody.Part,
        @Part("courseCreateRequestDto") courseCreateRequestDto: RequestBody,
    ): Response<ResponsePostCourseDTO>
}