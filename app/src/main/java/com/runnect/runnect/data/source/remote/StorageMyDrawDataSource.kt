package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.api.KCourseService
import com.runnect.runnect.data.model.ResponseGetCourseDto
import retrofit2.Response

class StorageMyDrawDataSource(private val courseService: KCourseService) {
    suspend fun getMyDrawCourseList(): Response<ResponseGetCourseDto> =
        courseService.getCourseList()
}