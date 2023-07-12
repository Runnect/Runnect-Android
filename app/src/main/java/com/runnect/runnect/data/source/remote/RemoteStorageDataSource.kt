package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.service.CourseService
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.model.RequestPutMyDrawDto
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.ResponseGetScrapDto
import com.runnect.runnect.data.model.ResponsePutMyDrawDto
import retrofit2.Response
import javax.inject.Inject

//여기 있는 것들 싹 다 CourseDataSource로 옮겨도 될 것 같은데 일단 하던 작업 마무리 짓는 게 우선이라 일단 냅둠
class RemoteStorageDataSource @Inject constructor(private val courseService: CourseService) {
    suspend fun getMyDrawCourse(): Response<ResponseGetCourseDto> =
        courseService.getCourseList()

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawDto): Response<ResponsePutMyDrawDto> =
        courseService.deleteMyDrawCourse(deleteCourseList)

    suspend fun getMyScrapCourse(): Response<ResponseGetScrapDto> =
        courseService.getScrapList()

    suspend fun postMyScrapCourse(requestCourseScrap: RequestCourseScrap): Response<ResponseCourseScrap> =
        courseService.postScrap(requestCourseScrap)
}