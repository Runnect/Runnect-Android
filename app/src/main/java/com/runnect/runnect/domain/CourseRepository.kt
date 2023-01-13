package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.CourseDetailDTO
import com.runnect.runnect.data.dto.CourseLoadInfoDTO
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.data.dto.RecommendCourseDTO
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestUploadMyCourse
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.response.ResponseCourseSearch
import com.runnect.runnect.data.dto.response.ResponseUploadMyCourse

interface CourseRepository {
    suspend fun getRecommendCourse():MutableList<RecommendCourseDTO>
    suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap):ResponseCourseScrap
    suspend fun getCourseSearch(keyword:String): MutableList<CourseSearchDTO>
    suspend fun getCourseDetail(publicCourseId:Int):CourseDetailDTO
    suspend fun getMyCourseLoad():MutableList<CourseLoadInfoDTO>
    suspend fun postUploadMyCourse(requestUploadMyCourse: RequestUploadMyCourse):ResponseUploadMyCourse
}