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
import com.runnect.runnect.data.source.remote.CourseDataSource
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.util.extension.toData

class CourseRepositoryImpl(private val courseDataSource: CourseDataSource) : CourseRepository {
    override suspend fun getRecommendCourse(): MutableList<RecommendCourseDTO> {
        val recommendCourse = mutableListOf<RecommendCourseDTO>()
        for (i in courseDataSource.getRecommendCourse().data.publicCourses) {
            recommendCourse.add(i.toData())
        }
        return recommendCourse
    }

    override suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap): ResponseCourseScrap {
        return courseDataSource.postCourseScrap(requestCourseScrap)
    }

    override suspend fun getCourseSearch(keyword: String): MutableList<CourseSearchDTO> {
        val searchPublicCourse = mutableListOf<CourseSearchDTO>()
        for (i in courseDataSource.getCourseSearch(keyword).data.publicCourses) {
            searchPublicCourse.add(i.toData())
        }
        return searchPublicCourse
    }

    override suspend fun getCourseDetail(publicCourseId: Int): CourseDetailDTO {
        return courseDataSource.getCourseDetail(publicCourseId).data.toData()
    }

    override suspend fun getMyCourseLoad(): MutableList<CourseLoadInfoDTO> {
        val myCourseLoad = mutableListOf<CourseLoadInfoDTO>()
        for (i in courseDataSource.getMyCourseLoad().data.privateCourses) {
            myCourseLoad.add(i.toData())
        }
        return myCourseLoad
    }

    override suspend fun postUploadMyCourse(requestUploadMyCourse: RequestUploadMyCourse): ResponseUploadMyCourse {
        return courseDataSource.postUploadMyCourse(requestUploadMyCourse)
    }

    override suspend fun patchUpdatePublicCourse(
        publicCourseId: Int,
        requestUpdatePublicCourse: RequestUpdatePublicCourse
    ): ResponseUpdatePublicCourse {
        return courseDataSource.patchUpdatePublicCourse(publicCourseId, requestUpdatePublicCourse)
    }

}