package com.runnect.runnect.data.repository

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
import com.runnect.runnect.data.source.remote.RemoteCourseDataSource
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.util.extension.toData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(private val remoteCourseDataSource: RemoteCourseDataSource) : CourseRepository {
    override suspend fun getRecommendCourse(): MutableList<RecommendCourseDTO> {
        val recommendCourse = mutableListOf<RecommendCourseDTO>()
        for (i in remoteCourseDataSource.getRecommendCourse().data.publicCourses) {
            recommendCourse.add(i.toData())
        }
        return recommendCourse
    }

    override suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap): ResponseCourseScrap {
        return remoteCourseDataSource.postCourseScrap(requestCourseScrap)
    }

    override suspend fun getCourseSearch(keyword: String): MutableList<CourseSearchDTO> {
        val searchPublicCourse = mutableListOf<CourseSearchDTO>()
        for (i in remoteCourseDataSource.getCourseSearch(keyword).data.publicCourses) {
            searchPublicCourse.add(i.toData())
        }
        return searchPublicCourse
    }

    override suspend fun getCourseDetail(publicCourseId: Int): CourseDetailDTO {
        return remoteCourseDataSource.getCourseDetail(publicCourseId).data.toData()
    }

    override suspend fun getMyCourseLoad(): MutableList<CourseLoadInfoDTO> {
        val myCourseLoad = mutableListOf<CourseLoadInfoDTO>()
        for (i in remoteCourseDataSource.getMyCourseLoad().data.privateCourses) {
            myCourseLoad.add(i.toData())
        }
        return myCourseLoad
    }

    override suspend fun postUploadMyCourse(requestUploadMyCourse: RequestUploadMyCourse): ResponseUploadMyCourse {
        return remoteCourseDataSource.postUploadMyCourse(requestUploadMyCourse)
    }

    override suspend fun patchUpdatePublicCourse(
        publicCourseId: Int,
        requestUpdatePublicCourse: RequestUpdatePublicCourse
    ): ResponseUpdatePublicCourse {
        return remoteCourseDataSource.patchUpdatePublicCourse(publicCourseId, requestUpdatePublicCourse)
    }


    override suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawDto): Response<ResponsePutMyDrawDto> {
        return remoteCourseDataSource.deleteMyDrawCourse(deleteCourseList)
    }

    override suspend fun getMyDrawDetail(courseId: Int): Response<ResponseGetMyDrawDetailDto> {
        return remoteCourseDataSource.getMyDrawDetail(courseId)
    }

    override suspend fun postRecord(request: RequestPostRecordDto): Response<ResponsePostRecordDto> {
        return remoteCourseDataSource.postRecord(request)
    }

    override suspend fun uploadCourse(
        image: MultipartBody.Part,
        data: RequestBody
    ): Response<ResponsePostCourseDto> {
        return remoteCourseDataSource.uploadCourse(image,data)
    }

    // 나중에 StorageRepository 없애고나서 살려줄 예정
//    override suspend fun getCourseList(): Response<ResponseGetCourseDto> {
//        return courseDataSource.getCourseList()
//    }
//
//    override suspend fun getScrapList(): Response<ResponseGetScrapDto> {
//        return courseDataSource.getScrapList()
//    }

}