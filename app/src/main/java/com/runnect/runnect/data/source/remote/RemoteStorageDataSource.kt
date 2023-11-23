package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.service.CourseService
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetCourseDTO
import com.runnect.runnect.data.dto.response.ResponseGetScrapDTO
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawDTO
import com.runnect.runnect.data.dto.response.base.BaseResponse
import retrofit2.Response
import javax.inject.Inject

class RemoteStorageDataSource @Inject constructor(private val courseService: CourseService) {
    suspend fun getMyDrawCourse(): Response<ResponseGetCourseDTO> =
        courseService.getCourseList()

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Response<ResponsePutMyDrawDTO> =
        courseService.deleteMyDrawCourse(deleteCourseList)

    suspend fun getMyScrapCourse(): Response<ResponseGetScrapDTO> =
        courseService.getScrapList()
}