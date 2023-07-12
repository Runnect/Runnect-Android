package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.CourseDetailDTO
import com.runnect.runnect.data.dto.CourseLoadInfoDTO
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.data.dto.RecommendCourseDTO
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestUpdatePublicCourse
import com.runnect.runnect.data.dto.request.RequestUploadMyCourse
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.response.ResponseUpdatePublicCourse
import com.runnect.runnect.data.dto.response.ResponseUploadMyCourse
import com.runnect.runnect.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface CourseRepository {
    suspend fun getRecommendCourse(): MutableList<RecommendCourseDTO>
    suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap): ResponseCourseScrap
    suspend fun getCourseSearch(keyword: String): MutableList<CourseSearchDTO>
    suspend fun getCourseDetail(publicCourseId: Int): CourseDetailDTO
    suspend fun getMyCourseLoad(): MutableList<CourseLoadInfoDTO>
    suspend fun postUploadMyCourse(requestUploadMyCourse: RequestUploadMyCourse): ResponseUploadMyCourse
    suspend fun patchUpdatePublicCourse(
        publicCourseId: Int,
        requestUpdatePublicCourse: RequestUpdatePublicCourse
    ): ResponseUpdatePublicCourse


    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawDto): Response<ResponsePutMyDrawDto>
    suspend fun getMyDrawDetail(courseId: Int): Response<ResponseGetMyDrawDetailDto>
    suspend fun postRecord(request: RequestPostRecordDto): Response<ResponsePostRecordDto>
    suspend fun uploadCourse(image: MultipartBody.Part, data: RequestBody): Response<ResponsePostCourseDto>

//   나중에 StorageRepository 없애고나서 살려줄 예정
//    suspend fun getCourseList(): Response<ResponseGetCourseDto>
//    suspend fun getScrapList(): Response<ResponseGetScrapDto>
}