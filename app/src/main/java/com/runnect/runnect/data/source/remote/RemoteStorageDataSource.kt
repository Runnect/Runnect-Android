package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.service.CourseService
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyScrapCourse
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawCourse
import retrofit2.Response
import javax.inject.Inject

class RemoteStorageDataSource @Inject constructor(private val courseService: CourseService) {
    suspend fun getMyDrawCourse(): Response<ResponseGetMyDrawCourse> =
        courseService.getCourseList()

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Response<ResponsePutMyDrawCourse> =
        courseService.deleteMyDrawCourse(deleteCourseList)

    suspend fun getMyScrapCourse(): Response<ResponseGetMyScrapCourse> =
        courseService.getScrapList()
}