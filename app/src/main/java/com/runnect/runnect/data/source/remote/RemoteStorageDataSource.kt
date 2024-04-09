package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyScrapCourse
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawCourse
import com.runnect.runnect.data.service.CourseV2Service
import javax.inject.Inject

class RemoteStorageDataSource @Inject constructor(
    private val courseV2Service: CourseV2Service,
) {
    suspend fun getMyDrawCourse(): Result<ResponseGetMyDrawCourse> =
        courseV2Service.getDrawCourseList()

    suspend fun getMyScrapCourse(): Result<ResponseGetMyScrapCourse> =
        courseV2Service.getScrapCourseList()

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Result<ResponsePutMyDrawCourse> =
        courseV2Service.deleteMyDrawCourse(deleteCourseList)
}