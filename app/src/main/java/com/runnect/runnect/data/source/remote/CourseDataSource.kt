package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.api.PCourseService
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestUpdatePublicCourse
import com.runnect.runnect.data.dto.request.RequestUploadMyCourse
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.response.ResponseRecommendCourse
import javax.inject.Inject

class CourseDataSource @Inject constructor(private val courseService: PCourseService) {
    suspend fun getRecommendCourse(): ResponseRecommendCourse = courseService.getRecommendCourse()
    suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap): ResponseCourseScrap =
        courseService.postCourseScrap(requestCourseScrap)

    suspend fun getCourseSearch(keyword: String) = courseService.getCourseSearch(keyword)
    suspend fun getCourseDetail(publicCourseId: Int) = courseService.getCourseDetail(publicCourseId)
    suspend fun getMyCourseLoad() = courseService.getMyCourseLoad()
    suspend fun postUploadMyCourse(requestUploadMyCourse: RequestUploadMyCourse) =
        courseService.postUploadMyCourse(requestUploadMyCourse)

    suspend fun patchUpdatePublicCourse(
        publicCourseId: Int,
        requestUpdatePublicCourse: RequestUpdatePublicCourse
    ) = courseService.patchUpdatePublicCourse(publicCourseId, requestUpdatePublicCourse)
}