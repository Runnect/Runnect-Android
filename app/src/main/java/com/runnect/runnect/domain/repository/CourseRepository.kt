package com.runnect.runnect.domain.repository

import com.runnect.runnect.data.dto.CourseLoadInfoDTO
import com.runnect.runnect.data.dto.request.RequestPatchPublicCourse
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.request.RequestPostPublicCourse
import com.runnect.runnect.data.dto.request.RequestPostRunningHistory
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetail
import com.runnect.runnect.data.dto.response.ResponsePostDiscoverUpload
import com.runnect.runnect.data.dto.response.ResponsePostMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponsePostMyHistory
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawCourse
import com.runnect.runnect.domain.entity.CourseDetail
import com.runnect.runnect.domain.entity.DiscoverSearchCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.*
import com.runnect.runnect.domain.entity.EditableCourseDetail
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface CourseRepository {
    suspend fun getMarathonCourse(): Result<List<MarathonCourse>?>

    suspend fun getRecommendCourse(pageNo: String, ordering: String): Result<List<RecommendCourse>?>

    suspend fun getCourseSearch(keyword: String): Result<List<DiscoverSearchCourse>?>

    suspend fun getMyCourseLoad(): MutableList<CourseLoadInfoDTO>

    suspend fun postUploadMyCourse(requestPostPublicCourse: RequestPostPublicCourse): ResponsePostDiscoverUpload

    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Response<ResponsePutMyDrawCourse>

    suspend fun getMyDrawDetail(courseId: Int): Response<ResponseGetMyDrawDetail>

    suspend fun postRecord(request: RequestPostRunningHistory): Response<ResponsePostMyHistory>

    suspend fun uploadCourse(
        image: MultipartBody.Part, courseCreateRequestDto: RequestBody
    ): Response<ResponsePostMyDrawCourse>
    suspend fun getCourseDetail(publicCourseId: Int): Result<CourseDetail?>

    suspend fun patchPublicCourse(
        publicCourseId: Int,
        requestPatchPublicCourse: RequestPatchPublicCourse
    ): Result<EditableCourseDetail?>

    suspend fun postCourseScrap(requestPostCourseScrap: RequestPostCourseScrap): Result<Unit?>
}