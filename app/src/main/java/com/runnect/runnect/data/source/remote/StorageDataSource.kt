package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.api.KCourseService
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.model.RequestPutMyDrawDto
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.ResponseGetScrapDto
import com.runnect.runnect.data.model.ResponsePutMyDrawDto
import retrofit2.Response
import javax.inject.Inject

class StorageDataSource @Inject constructor(private val courseService: KCourseService) {
    suspend fun getMyDrawCourse(): Response<ResponseGetCourseDto> =
        courseService.getCourseList()

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawDto): Response<ResponsePutMyDrawDto> =
        courseService.deleteMyDrawCourse(deleteCourseList)

    suspend fun getMyScrapCourse(): Response<ResponseGetScrapDto> =
        courseService.getScrapList()

    suspend fun postMyScrapCourse(requestCourseScrap: RequestCourseScrap): Response<ResponseCourseScrap> =
        courseService.postCourseScrap(requestCourseScrap)
}