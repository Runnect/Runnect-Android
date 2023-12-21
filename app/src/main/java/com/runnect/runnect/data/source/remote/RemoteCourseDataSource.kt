package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.service.CourseService
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.request.RequestPostRunningHistory
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.request.RequestPatchPublicCourse
import com.runnect.runnect.data.dto.request.RequestPostPublicCourse
import com.runnect.runnect.data.dto.response.ResponseGetCourseDetail
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverMarathon
import com.runnect.runnect.data.dto.response.ResponsePatchPublicCourse
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverRecommend
import com.runnect.runnect.data.dto.response.base.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class RemoteCourseDataSource @Inject constructor(
    private val courseService: CourseService
) {
    suspend fun getMarathonCourse(): BaseResponse<ResponseGetDiscoverMarathon> =
        courseService.getMarathonCourse()

    suspend fun getRecommendCourse(
        pageNo: String,
        ordering: String
    ): BaseResponse<ResponseGetDiscoverRecommend> =
        courseService.getRecommendCourse(pageNo = pageNo, ordering = ordering)

    suspend fun postCourseScrap(requestPostCourseScrap: RequestPostCourseScrap): BaseResponse<Unit> =
        courseService.postCourseScrap(requestPostCourseScrap)

    suspend fun getCourseSearch(keyword: String) = courseService.getCourseSearch(keyword)

    suspend fun getCourseDetail(publicCourseId: Int): BaseResponse<ResponseGetCourseDetail> =
        courseService.getCourseDetail(publicCourseId)

    suspend fun getMyCourseLoad() = courseService.getMyCourseLoad()

    suspend fun postUploadMyCourse(requestPostPublicCourse: RequestPostPublicCourse) =
        courseService.postUploadMyCourse(requestPostPublicCourse)

    suspend fun patchPublicCourse(
        publicCourseId: Int,
        requestPatchPublicCourse: RequestPatchPublicCourse
    ): BaseResponse<ResponsePatchPublicCourse> =
        courseService.patchPublicCourse(publicCourseId, requestPatchPublicCourse)

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse) =
        courseService.deleteMyDrawCourse(deleteCourseList)

    suspend fun getMyDrawDetail(courseId: Int) = courseService.getMyDrawDetail(courseId)

    suspend fun postRecord(request: RequestPostRunningHistory) = courseService.postRecord(request)

    suspend fun uploadCourse(image: MultipartBody.Part, courseCreateRequestDto: RequestBody) =
        courseService.uploadCourse(image, courseCreateRequestDto)
}