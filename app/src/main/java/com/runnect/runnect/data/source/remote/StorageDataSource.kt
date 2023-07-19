package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.api.KCourseService
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.response.RequestPutMyDrawDto
import com.runnect.runnect.data.dto.response.ResponseGetCourseDto
import com.runnect.runnect.data.dto.response.ResponseGetScrapDto
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawDto
import retrofit2.Response

class StorageDataSource(private val courseService: KCourseService) {
    suspend fun getMyDrawCourse(): Response<ResponseGetCourseDto> =
        courseService.getCourseList()

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawDto): Response<ResponsePutMyDrawDto> =
        courseService.deleteMyDrawCourse(deleteCourseList)

    suspend fun getMyScrapCourse(): Response<ResponseGetScrapDto> =
        courseService.getScrapList()

    suspend fun postMyScrapCourse(requestCourseScrap: RequestCourseScrap): Response<ResponseCourseScrap> =
        courseService.postCourseScrap(requestCourseScrap)
}