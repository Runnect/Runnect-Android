package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.response.ResponseGetDiscoverMarathon
import retrofit2.http.GET

interface CourseV2Service {
    @GET("/api/public-course/marathon")
    suspend fun getMarathonCourse(): Result<ResponseGetDiscoverMarathon>
}