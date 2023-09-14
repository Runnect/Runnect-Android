package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.CourseDetailDTO
import com.runnect.runnect.data.dto.CourseLoadInfoDTO
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.data.dto.RecommendCourseDTO
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestPostRecordDTO
import com.runnect.runnect.data.dto.request.RequestPutMyDrawDTO
import com.runnect.runnect.data.dto.request.RequestUpdatePublicCourse
import com.runnect.runnect.data.dto.request.RequestUploadMyCourse
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetailDTO
import com.runnect.runnect.data.dto.response.ResponsePostCourseDTO
import com.runnect.runnect.data.dto.response.ResponsePostRecordDTO
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawDTO
import com.runnect.runnect.data.dto.response.ResponseUpdatePublicCourse
import com.runnect.runnect.data.dto.response.ResponseUploadMyCourse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface CourseRepository {
    suspend fun getRecommendCourse(pageNo: String?): MutableList<RecommendCourseDTO>
    suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap): ResponseCourseScrap
    suspend fun getCourseSearch(keyword: String): MutableList<CourseSearchDTO>
    suspend fun getCourseDetail(publicCourseId: Int): CourseDetailDTO
    suspend fun getMyCourseLoad(): MutableList<CourseLoadInfoDTO>
    suspend fun postUploadMyCourse(requestUploadMyCourse: RequestUploadMyCourse): ResponseUploadMyCourse
    suspend fun patchUpdatePublicCourse(
        publicCourseId: Int, requestUpdatePublicCourse: RequestUpdatePublicCourse
    ): ResponseUpdatePublicCourse


    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawDTO): Response<ResponsePutMyDrawDTO>
    suspend fun getMyDrawDetail(courseId: Int): Response<ResponseGetMyDrawDetailDTO>
    suspend fun postRecord(request: RequestPostRecordDTO): Response<ResponsePostRecordDTO>
    suspend fun uploadCourse(
        image: MultipartBody.Part, data: RequestBody
    ): Response<ResponsePostCourseDTO>
}