package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.response.ResponseGetDiscoverMarathon
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverRecommend
import retrofit2.http.GET
import retrofit2.http.Query

interface CourseV2Service {
    @GET("/api/public-course/marathon")
    suspend fun getMarathonCourse(): Result<ResponseGetDiscoverMarathon>

    @GET("/api/public-course")
    suspend fun getRecommendCourse(
        @Query("pageNo") pageNo: String,
        @Query("sort") sort: String
    ): Result<ResponseGetDiscoverRecommend>
}