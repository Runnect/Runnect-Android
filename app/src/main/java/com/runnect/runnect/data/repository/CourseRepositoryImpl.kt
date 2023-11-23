package com.runnect.runnect.data.repository

import com.runnect.runnect.domain.entity.CourseDetail
import com.runnect.runnect.data.dto.CourseLoadInfoDTO
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.data.dto.RecommendCourseDTO
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
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
import com.runnect.runnect.data.source.remote.RemoteCourseDataSource
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.util.extension.toData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(private val remoteCourseDataSource: RemoteCourseDataSource) :
    CourseRepository {
    override suspend fun getRecommendCourse(pageNo: String?): MutableList<RecommendCourseDTO> {
        return remoteCourseDataSource.getRecommendCourse(pageNo = pageNo).data.publicCourses.map { it.toData() }
            .toMutableList()
    }

    override suspend fun getCourseSearch(keyword: String): MutableList<CourseSearchDTO> {
        return remoteCourseDataSource.getCourseSearch(keyword = keyword).data.publicCourses.map { it.toData() }
            .toMutableList()
    }

    override suspend fun getMyCourseLoad(): MutableList<CourseLoadInfoDTO> {
        return remoteCourseDataSource.getMyCourseLoad().data.privateCourses.map { it.toData() }
            .toMutableList()
    }

    override suspend fun postUploadMyCourse(requestPostPublicCourse: RequestPostPublicCourse): ResponseUploadMyCourse {
        return remoteCourseDataSource.postUploadMyCourse(requestPostPublicCourse = requestPostPublicCourse)
    }

    override suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Response<ResponsePutMyDrawDTO> {
        return remoteCourseDataSource.deleteMyDrawCourse(deleteCourseList = deleteCourseList)
    }

    override suspend fun getMyDrawDetail(courseId: Int): Response<ResponseGetMyDrawDetailDTO> {
        return remoteCourseDataSource.getMyDrawDetail(courseId = courseId)
    }

    override suspend fun postRecord(request: RequestPostRunningHistory): Response<ResponsePostRecordDTO> {
        return remoteCourseDataSource.postRecord(request = request)
    }

    override suspend fun uploadCourse(
        image: MultipartBody.Part,
        data: RequestBody
    ): Response<ResponsePostCourseDTO> {
        return remoteCourseDataSource.uploadCourse(image = image, data = data)
    }

    // todo: ----------------------------------------------------- runCatching

    override suspend fun getCourseDetail(
        publicCourseId: Int
    ): Result<CourseDetail?> = runCatching {
        remoteCourseDataSource.getCourseDetail(publicCourseId = publicCourseId).data?.toCourseDetail()
    }

    override suspend fun patchPublicCourse(
        publicCourseId: Int,
        requestPatchPublicCourse: RequestPatchPublicCourse
    ): Result<PublicCourse?> = runCatching {
        remoteCourseDataSource.patchPublicCourse(
            publicCourseId = publicCourseId,
            requestPatchPublicCourse = requestPatchPublicCourse
        ).data?.publicCourse
    }

    override suspend fun postCourseScrap(
        requestPostCourseScrap: RequestPostCourseScrap
    ): Result<Unit?> = runCatching {
        remoteCourseDataSource.postCourseScrap(requestPostCourseScrap = requestPostCourseScrap).data
    }
}