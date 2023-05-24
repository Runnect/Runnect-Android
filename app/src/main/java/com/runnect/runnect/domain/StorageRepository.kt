package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestUploadMyCourse
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.response.ResponseUploadMyCourse
import com.runnect.runnect.data.model.MyDrawCourse
import com.runnect.runnect.data.model.MyScrapCourse
import com.runnect.runnect.data.model.RequestPutMyDrawDto
import com.runnect.runnect.data.model.ResponsePutMyDrawDto
import retrofit2.Response

interface StorageRepository {
    suspend fun getMyDrawCourse(): List<MyDrawCourse>?
    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawDto) : Response<ResponsePutMyDrawDto>
    suspend fun getMyScrapCourse(): List<MyScrapCourse>?
    suspend fun postMyScrapCourse(requestCourseScrap: RequestCourseScrap): Response<ResponseCourseScrap>
}