package com.runnect.runnect.data.repository

import com.runnect.runnect.domain.entity.CourseDetail
import com.runnect.runnect.data.dto.CourseLoadInfoDTO
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.request.RequestPostRunningHistory
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.request.RequestPatchPublicCourse
import com.runnect.runnect.data.dto.request.RequestPostPublicCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetail
import com.runnect.runnect.data.dto.response.ResponsePostMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponsePostMyHistory
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponsePostDiscoverUpload
import com.runnect.runnect.data.dto.response.ResponsePostScrap
import com.runnect.runnect.data.source.remote.RemoteCourseDataSource
import com.runnect.runnect.domain.entity.DiscoverSearchCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.*
import com.runnect.runnect.domain.entity.EditableCourseDetail
import com.runnect.runnect.domain.entity.RecommendCoursePagingData
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.util.extension.toData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(private val remoteCourseDataSource: RemoteCourseDataSource) :
    CourseRepository {
    override suspend fun getMarathonCourse(): Result<List<MarathonCourse>?> = runCatching {
        remoteCourseDataSource.getMarathonCourse().data?.toMarathonCourses()
    }

    override suspend fun getRecommendCourse(
        pageNo: String,
        sort: String
    ): Result<RecommendCoursePagingData?> = runCatching {
        val response = remoteCourseDataSource.getRecommendCourse(
            pageNo = pageNo,
            sort = sort
        ).data

        response?.let {
            RecommendCoursePagingData(response.isEnd, response.toRecommendCourses())
        }
    }

    override suspend fun getCourseSearch(keyword: String): Result<List<DiscoverSearchCourse>?> =
        runCatching {
            remoteCourseDataSource.getCourseSearch(keyword = keyword).data?.toDiscoverSearchCourses()
        }

    override suspend fun getMyCourseLoad(): MutableList<CourseLoadInfoDTO> {
        return remoteCourseDataSource.getMyCourseLoad().data.privateCourses.map { it.toData() }
            .toMutableList()
    }

    override suspend fun postUploadMyCourse(requestPostPublicCourse: RequestPostPublicCourse): ResponsePostDiscoverUpload {
        return remoteCourseDataSource.postUploadMyCourse(requestPostPublicCourse = requestPostPublicCourse)
    }

    override suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Response<ResponsePutMyDrawCourse> {
        return remoteCourseDataSource.deleteMyDrawCourse(deleteCourseList = deleteCourseList)
    }

    override suspend fun getMyDrawDetail(courseId: Int): Response<ResponseGetMyDrawDetail> {
        return remoteCourseDataSource.getMyDrawDetail(courseId = courseId)
    }

    override suspend fun postRecord(request: RequestPostRunningHistory): Response<ResponsePostMyHistory> {
        return remoteCourseDataSource.postRecord(request = request)
    }

    override suspend fun uploadCourse(
        image: MultipartBody.Part,
        courseCreateRequestDto: RequestBody
    ): Response<ResponsePostMyDrawCourse> {
        return remoteCourseDataSource.uploadCourse(
            image = image,
            courseCreateRequestDto = courseCreateRequestDto
        )
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
    ): Result<EditableCourseDetail?> = runCatching {
        remoteCourseDataSource.patchPublicCourse(
            publicCourseId = publicCourseId,
            requestPatchPublicCourse = requestPatchPublicCourse
        ).data?.toEditableCourseDetail()
    }

    override suspend fun postCourseScrap(
        requestPostCourseScrap: RequestPostCourseScrap
    ): Result<ResponsePostScrap?> = runCatching {
        remoteCourseDataSource.postCourseScrap(requestPostCourseScrap = requestPostCourseScrap).data
    }
}