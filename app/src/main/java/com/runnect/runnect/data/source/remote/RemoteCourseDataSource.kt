package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.dto.request.RequestPatchPublicCourse
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.request.RequestPostPublicCourse
import com.runnect.runnect.data.dto.request.RequestPostRunningHistory
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetCourseDetail
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverMarathon
import com.runnect.runnect.data.dto.response.ResponseGetDiscoverRecommend
import com.runnect.runnect.data.dto.response.ResponsePatchPublicCourse
import com.runnect.runnect.data.dto.response.ResponsePostScrap
import com.runnect.runnect.data.service.CourseService
import com.runnect.runnect.data.service.CourseV2Service
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class RemoteCourseDataSource @Inject constructor(
    private var courseV2Service: CourseV2Service,
    private val courseService: CourseService
) {
    suspend fun getMarathonCourse(): Result<ResponseGetDiscoverMarathon> =
        courseV2Service.getMarathonCourse()

    suspend fun getRecommendCourse(
        pageNo: String,
        sort: String
    ): Result<ResponseGetDiscoverRecommend> =
        courseV2Service.getRecommendCourse(pageNo = pageNo, sort = sort)

    suspend fun postCourseScrap(requestPostCourseScrap: RequestPostCourseScrap): Result<ResponsePostScrap> =
        courseV2Service.postCourseScrap(requestPostCourseScrap)

    suspend fun getCourseSearch(keyword: String) = courseV2Service.getCourseSearch(keyword)

    suspend fun getCourseDetail(publicCourseId: Int): Result<ResponseGetCourseDetail> =
        courseV2Service.getCourseDetail(publicCourseId)

    suspend fun getMyCourseLoad() = courseV2Service.getMyCourseLoad()

    suspend fun postUploadMyCourse(requestPostPublicCourse: RequestPostPublicCourse) =
        courseV2Service.postUploadMyCourse(requestPostPublicCourse)

    suspend fun patchPublicCourse(
        publicCourseId: Int,
        requestPatchPublicCourse: RequestPatchPublicCourse
    ): Result<ResponsePatchPublicCourse> =
        courseV2Service.patchPublicCourse(publicCourseId, requestPatchPublicCourse)

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse) =
        courseV2Service.deleteMyDrawCourse(deleteCourseList)

    suspend fun getMyDrawDetail(courseId: Int) = courseV2Service.getMyDrawDetail(courseId)

    suspend fun postRecord(request: RequestPostRunningHistory) = courseService.postRecord(request)

    suspend fun uploadCourse(image: MultipartBody.Part, data: RequestBody) =
        courseService.uploadCourse(image, data)
}