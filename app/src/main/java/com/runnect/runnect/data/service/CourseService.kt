package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.request.RequestPostRunningHistory
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.request.RequestPatchPublicCourse
import com.runnect.runnect.data.dto.request.RequestPostPublicCourse
import com.runnect.runnect.data.dto.response.*
import com.runnect.runnect.data.dto.response.base.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CourseService {
    @GET("/api/public-course")
    suspend fun getRecommendCourse(
        @Query("pageNo") pageNo: String,
        @Query("ordering") ordering: String
    ): BaseResponse<ResponseGetDiscoverRecommend>

    @POST("/api/scrap")
    suspend fun postCourseScrap(
        @Body requestPostCourseScrap: RequestPostCourseScrap,
    ): BaseResponse<Unit>

    @GET("/api/public-course/search?")
    suspend fun getCourseSearch(
        @Query("keyword") keyword: String,
    ): ResponseGetDiscoverSearch

    @GET("/api/public-course/detail/{publicCourseId}")
    suspend fun getCourseDetail(
        @Path("publicCourseId") publicCourseId: Int,
    ): BaseResponse<ResponseGetCourseDetail>

    @GET("/api/course/private/user")
    suspend fun getMyCourseLoad(
    ): ResponseGetDiscoverPick

    @POST("/api/public-course")
    suspend fun postUploadMyCourse(
        @Body requestPostPublicCourse: RequestPostPublicCourse,
    ): ResponsePostDiscoverUpload

    @PATCH("/api/public-course/{publicCourseId}")
    suspend fun patchPublicCourse(
        @Path("publicCourseId") publicCourseId: Int,
        @Body requestPatchPublicCourse: RequestPatchPublicCourse
    ): BaseResponse<ResponsePatchPublicCourse>

    // {id}와 같이 동적인 경로 변수가 없다면 @Path 생략 가능
    //내가 그린 코스 수정
    @PUT("/api/course")
    suspend fun deleteMyDrawCourse(
        @Body deleteCourseList: RequestPutMyDrawCourse
    ): Response<ResponsePutMyDrawCourse>

    //보관함 내가 그린 코스 가져오기
    @GET("/api/course/user")
    suspend fun getCourseList(
    ): Response<ResponseGetMyDrawCourse>

    //보관함 스크랩 코스 가져오기
    @GET("/api/scrap/user")
    suspend fun getScrapList(
    ): Response<ResponseGetMyScrapCourse>

    //내가 그린 코스 Detail 가져오기
    @GET("/api/course/detail/{courseId}")
    suspend fun getMyDrawDetail(
        @Path("courseId") courseId: Int,
    ): Response<ResponseGetMyDrawDetail>

    //기록 업로드
    @POST("/api/record")
    suspend fun postRecord(
        @Body request: RequestPostRunningHistory
    ): Response<ResponsePostMyHistory>

    //코스 업로드
    @Multipart
    @POST("/api/course")
    suspend fun uploadCourse(
        @Part image: MultipartBody.Part,
        @Part("data") data: RequestBody,
    ): Response<ResponsePostMyDrawCourse>
}