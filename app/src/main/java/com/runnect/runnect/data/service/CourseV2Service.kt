package com.runnect.runnect.data.service

import com.runnect.runnect.data.network.ApiResult
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverMarathon
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface CourseV2Service {
    @GET("/api/public-course/marathon")
    fun getMarathonCourse(): Flow<ApiResult<ResponseGetDiscoverMarathon>>
}