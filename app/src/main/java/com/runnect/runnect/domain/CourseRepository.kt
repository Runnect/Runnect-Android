package com.runnect.runnect.domain

import com.runnect.runnect.domain.entity.CourseDetail
import com.runnect.runnect.data.dto.CourseLoadInfoDTO
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.data.dto.RecommendCourseDTO
import com.runnect.runnect.data.dto.request.RequestPostScrap
import com.runnect.runnect.data.dto.request.RequestPostRunningHistory
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.request.RequestPatchPublicCourse
import com.runnect.runnect.data.dto.request.RequestPostPublicCourse
import com.runnect.runnect.data.dto.response.PublicCourse
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetailDTO
import com.runnect.runnect.data.dto.response.ResponsePostCourseDTO
import com.runnect.runnect.data.dto.response.ResponsePostRecordDTO
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawDTO
import com.runnect.runnect.data.dto.response.ResponseUploadMyCourse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface CourseRepository {
    suspend fun getRecommendCourse(pageNo: String?): MutableList<RecommendCourseDTO>

    suspend fun postCourseScrap(requestPostScrap: RequestPostScrap): ResponseCourseScrap

    suspend fun getCourseSearch(keyword: String): MutableList<CourseSearchDTO>

    suspend fun getCourseDetail(publicCourseId: Int): Result<CourseDetail?>

    suspend fun getMyCourseLoad(): MutableList<CourseLoadInfoDTO>

    suspend fun postUploadMyCourse(requestPostPublicCourse: RequestPostPublicCourse): ResponseUploadMyCourse

    suspend fun patchPublicCourse(
        publicCourseId: Int,
        requestPatchPublicCourse: RequestPatchPublicCourse
    ): Result<PublicCourse?>

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Response<ResponsePutMyDrawDTO>

    suspend fun getMyDrawDetail(courseId: Int): Response<ResponseGetMyDrawDetailDTO>

    suspend fun postRecord(request: RequestPostRunningHistory): Response<ResponsePostRecordDTO>

    suspend fun uploadCourse(
        image: MultipartBody.Part, data: RequestBody
    ): Response<ResponsePostCourseDTO>
}