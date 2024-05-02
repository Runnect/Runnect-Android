package com.runnect.runnect.data.repository

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
import com.runnect.runnect.data.network.mapToFlowResult
import com.runnect.runnect.data.source.remote.RemoteCourseDataSource
import com.runnect.runnect.domain.entity.CourseDetail
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverSearchCourse
import com.runnect.runnect.domain.entity.DiscoverUploadCourse
import com.runnect.runnect.domain.entity.EditableCourseDetail
import com.runnect.runnect.domain.entity.PostScrap
import com.runnect.runnect.domain.entity.RecommendCoursePagingData
import com.runnect.runnect.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(private val remoteCourseDataSource: RemoteCourseDataSource) :
    CourseRepository {

    override suspend fun getMarathonCourse(): Flow<Result<List<MarathonCourse>>> {
        return remoteCourseDataSource.getMarathonCourse().mapToFlowResult {
            it.toMarathonCourses()
        }
    }

    override suspend fun getRecommendCourse(
        pageNo: String,
        sort: String
    ): Flow<Result<RecommendCoursePagingData>> {
        return remoteCourseDataSource.getRecommendCourse(
            pageNo = pageNo,
            sort = sort
        ).mapToFlowResult {
            RecommendCoursePagingData(it.isEnd, it.toRecommendCourses())
        }
    }

    override suspend fun getCourseSearch(keyword: String): Flow<Result<List<DiscoverSearchCourse>>> =
        remoteCourseDataSource.getCourseSearch(keyword = keyword).mapToFlowResult {
            it.toDiscoverSearchCourses()
        }

    override suspend fun getCourseDetail(publicCourseId: Int): Flow<Result<CourseDetail>> =
        remoteCourseDataSource.getCourseDetail(publicCourseId = publicCourseId).mapToFlowResult {
            it.toCourseDetail()
        }

    override suspend fun getMyCourseLoad(): Flow<Result<List<DiscoverUploadCourse>>> {
        return remoteCourseDataSource.getMyCourseLoad().mapToFlowResult {
            it.toUploadCourses()
        }
    }

    override suspend fun getMyDrawDetail(courseId: Int): Flow<Result<ResponseGetMyDrawDetail>> {
        return remoteCourseDataSource.getMyDrawDetail(courseId = courseId).mapToFlowResult { it }
    }

    override suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Flow<Result<ResponsePutMyDrawCourse>> {
        return remoteCourseDataSource.deleteMyDrawCourse(deleteCourseList = deleteCourseList).mapToFlowResult { it }
    }

    override suspend fun postCourseScrap(
        requestPostCourseScrap: RequestPostCourseScrap
    ): Flow<Result<PostScrap>> {
        return remoteCourseDataSource.postCourseScrap(requestPostCourseScrap = requestPostCourseScrap).mapToFlowResult {
            it.toPostScrap()
        }
    }

    override suspend fun postUploadMyCourse(requestPostPublicCourse: RequestPostPublicCourse): Flow<Result<ResponsePostDiscoverUpload>> {
        return remoteCourseDataSource.postUploadMyCourse(requestPostPublicCourse = requestPostPublicCourse).mapToFlowResult { it }
    }

    override suspend fun patchPublicCourse(
        publicCourseId: Int,
        requestPatchPublicCourse: RequestPatchPublicCourse
    ): Flow<Result<EditableCourseDetail>> =
        remoteCourseDataSource.patchPublicCourse(
            publicCourseId = publicCourseId,
            requestPatchPublicCourse = requestPatchPublicCourse
        ).mapToFlowResult {
            it.toEditableCourseDetail()
        }

    override suspend fun postRecord(request: RequestPostRunningHistory): Flow<Result<ResponsePostMyHistory>> {
        return remoteCourseDataSource.postRecord(request = request).mapToFlowResult { it }
    }

    override suspend fun uploadCourse(
        image: MultipartBody.Part,
        data: RequestBody
    ): Flow<Result<ResponsePostMyDrawCourse>> {
        return remoteCourseDataSource.uploadCourse(image = image, data = data).mapToFlowResult { it }
    }
}