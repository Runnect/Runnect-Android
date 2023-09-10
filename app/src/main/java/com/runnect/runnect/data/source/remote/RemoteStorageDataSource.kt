package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.service.CourseService
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.request.RequestPutMyDrawDTO
import com.runnect.runnect.data.dto.response.ResponseGetCourseDTO
import com.runnect.runnect.data.dto.response.ResponseGetScrapDTO
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawDTO
import retrofit2.Response
import javax.inject.Inject

class RemoteStorageDataSource @Inject constructor(private val courseService: CourseService) {
    suspend fun getMyDrawCourse(): Response<ResponseGetCourseDTO> =
        courseService.getCourseList()

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawDTO): Response<ResponsePutMyDrawDTO> =
        courseService.deleteMyDrawCourse(deleteCourseList)

    suspend fun getMyScrapCourse(): Response<ResponseGetScrapDTO> =
        courseService.getScrapList()

    suspend fun postMyScrapCourse(requestCourseScrap: RequestCourseScrap): Response<ResponseCourseScrap> =
        courseService.postScrap(requestCourseScrap)
}