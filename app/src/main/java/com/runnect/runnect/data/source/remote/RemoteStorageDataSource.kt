package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyScrapCourse
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawCourse
import com.runnect.runnect.data.service.CourseService
import javax.inject.Inject

class RemoteStorageDataSource @Inject constructor(
    private val courseService: CourseService,
) {
    suspend fun getMyDrawCourse(): Result<ResponseGetMyDrawCourse> =
        courseService.getDrawCourseList()

    suspend fun getMyScrapCourse(): Result<ResponseGetMyScrapCourse> =
        courseService.getScrapCourseList()

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Result<ResponsePutMyDrawCourse> =
        courseService.deleteMyDrawCourse(deleteCourseList)
}