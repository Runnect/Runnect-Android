package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.service.CourseService
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestPostRecordDTO
import com.runnect.runnect.data.dto.request.RequestPutMyDrawDTO
import com.runnect.runnect.data.dto.request.RequestPatchPublicCourseDto
import com.runnect.runnect.data.dto.request.RequestUploadMyCourse
import com.runnect.runnect.data.dto.response.ResponseGetCourseDetailDto
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.response.ResponsePatchPublicCourseDto
import com.runnect.runnect.data.dto.response.ResponseRecommendCourse
import com.runnect.runnect.data.dto.response.base.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class RemoteCourseDataSource @Inject constructor(private val courseService: CourseService) {
    suspend fun getRecommendCourse(pageNo: String?): ResponseRecommendCourse =
        courseService.getRecommendCourse(pageNo = pageNo)

    suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap): ResponseCourseScrap =
        courseService.postCourseScrap(requestCourseScrap)

    suspend fun getCourseSearch(keyword: String) = courseService.getCourseSearch(keyword)

    suspend fun getCourseDetail(publicCourseId: Int): BaseResponse<ResponseGetCourseDetailDto> =
        courseService.getCourseDetail(publicCourseId)

    suspend fun getMyCourseLoad() = courseService.getMyCourseLoad()

    suspend fun postUploadMyCourse(requestUploadMyCourse: RequestUploadMyCourse) =
        courseService.postUploadMyCourse(requestUploadMyCourse)

    suspend fun patchPublicCourse(
        publicCourseId: Int,
        requestPatchPublicCourseDto: RequestPatchPublicCourseDto
    ): BaseResponse<ResponsePatchPublicCourseDto> =
        courseService.patchPublicCourse(publicCourseId, requestPatchPublicCourseDto)

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawDTO) =
        courseService.deleteMyDrawCourse(deleteCourseList)

    suspend fun getMyDrawDetail(courseId: Int) = courseService.getMyDrawDetail(courseId)

    suspend fun postRecord(request: RequestPostRecordDTO) = courseService.postRecord(request)

    suspend fun uploadCourse(image: MultipartBody.Part, courseCreateRequestDto: RequestBody) =
        courseService.uploadCourse(image, courseCreateRequestDto)
}