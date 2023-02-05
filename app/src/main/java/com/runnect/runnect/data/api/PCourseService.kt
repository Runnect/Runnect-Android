package com.runnect.runnect.data.api

import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestUploadMyCourse
import com.runnect.runnect.data.dto.response.*
import retrofit2.http.*

interface PCourseService {
    @GET("/api/public-course")
    suspend fun getRecommendCourse(
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
    ): ResponseCourseDetail

    @GET("/api/course/private/user")
    suspend fun getMyCourseLoad(
    ): ResponseMyCourseLoad

    @POST("/api/public-course")
    suspend fun postUploadMyCourse(
        @Body requestUploadMyCourse: RequestUploadMyCourse,
    ): ResponseUploadMyCourse
}