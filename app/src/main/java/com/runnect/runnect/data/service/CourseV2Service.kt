package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverMarathon
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverRecommend
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverUploadCourse
import com.runnect.runnect.data.dto.response.ResponsePostScrap
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CourseV2Service {
    @GET("/api/public-course/marathon")
    suspend fun getMarathonCourse(): Result<ResponseGetDiscoverMarathon>

    @GET("/api/public-course")
    suspend fun getRecommendCourse(
        @Query("pageNo") pageNo: String,
        @Query("sort") sort: String
    ): Result<ResponseGetDiscoverRecommend>

    @GET("/api/course/private/user")
    suspend fun getMyCourseLoad(): Result<ResponseGetDiscoverUploadCourse>

    @POST("/api/scrap")
    suspend fun postCourseScrap(
        @Body requestPostCourseScrap: RequestPostCourseScrap,
    ): Result<ResponsePostScrap>
}