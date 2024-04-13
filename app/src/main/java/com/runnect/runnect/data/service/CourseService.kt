package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.request.RequestPatchPublicCourse
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.request.RequestPostPublicCourse
import com.runnect.runnect.data.dto.request.RequestPostRunningHistory
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetCourseDetail
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverMarathon
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverRecommend
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverSearch
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverUploadCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetail
import com.runnect.runnect.data.dto.response.ResponseGetMyScrapCourse
import com.runnect.runnect.data.dto.response.ResponsePatchPublicCourse
import com.runnect.runnect.data.dto.response.ResponsePostDiscoverUpload
import com.runnect.runnect.data.dto.response.ResponsePostMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponsePostMyHistory
import com.runnect.runnect.data.dto.response.ResponsePostScrap
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawCourse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseService {

    @GET("/api/public-course/search?")
    suspend fun getCourseSearch(
        @Query("keyword") keyword: String,
    ): Result<ResponseGetDiscoverSearch>

    @GET("/api/public-course/detail/{publicCourseId}")
    suspend fun getCourseDetail(
        @Path("publicCourseId") publicCourseId: Int,
    ): Result<ResponseGetCourseDetail>

    @GET("/api/public-course/marathon")
    suspend fun getMarathonCourse(): Result<ResponseGetDiscoverMarathon>

    @GET("/api/public-course")
    suspend fun getRecommendCourse(
        @Query("pageNo") pageNo: String,
        @Query("sort") sort: String
    ): Result<ResponseGetDiscoverRecommend>

    @GET("/api/course/private/user")
    suspend fun getMyCourseLoad(): Result<ResponseGetDiscoverUploadCourse>

    //내가 그린 코스 Detail 가져오기
    @GET("/api/course/detail/{courseId}")
    suspend fun getMyDrawDetail(
        @Path("courseId") courseId: Int,
    ): Result<ResponseGetMyDrawDetail>

    //보관함 내가 그린 코스 가져오기
    @GET("/api/course/user")
    suspend fun getDrawCourseList(): Result<ResponseGetMyDrawCourse>

    //보관함 스크랩 코스 가져오기
    @GET("/api/scrap/user")
    suspend fun getScrapCourseList(): Result<ResponseGetMyScrapCourse>

    @POST("/api/public-course")
    suspend fun postUploadMyCourse(
        @Body requestPostPublicCourse: RequestPostPublicCourse,
    ): Result<ResponsePostDiscoverUpload>

    @POST("/api/scrap")
    suspend fun postCourseScrap(
        @Body requestPostCourseScrap: RequestPostCourseScrap,
    ): Result<ResponsePostScrap>

    @PATCH("/api/public-course/{publicCourseId}")
    suspend fun patchPublicCourse(
        @Path("publicCourseId") publicCourseId: Int,
        @Body requestPatchPublicCourse: RequestPatchPublicCourse
    ): Result<ResponsePatchPublicCourse>

    //내가 그린 코스 삭제
    @PUT("/api/course")
    suspend fun deleteMyDrawCourse(
        @Body deleteCourseList: RequestPutMyDrawCourse
    ): Result<ResponsePutMyDrawCourse>

    //기록 업로드
    @POST("/api/record")
    suspend fun postRecord(
        @Body request: RequestPostRunningHistory
    ): Result<ResponsePostMyHistory>

    //코스 업로드
    @Multipart
    @POST("/api/course")
    suspend fun uploadCourse(
        @Part image: MultipartBody.Part,
        @Part("data") data: RequestBody,
    ): Result<ResponsePostMyDrawCourse>
}