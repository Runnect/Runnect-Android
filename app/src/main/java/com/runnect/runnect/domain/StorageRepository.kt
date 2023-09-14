package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.dto.MyDrawCourse
import com.runnect.runnect.data.dto.MyScrapCourse
import com.runnect.runnect.data.dto.request.RequestPutMyDrawDTO
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawDTO
import retrofit2.Response

interface StorageRepository {
    suspend fun getMyDrawCourse(): MutableList<MyDrawCourse>?
    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawDTO) : Response<ResponsePutMyDrawDTO>
    suspend fun getMyScrapCourse(): MutableList<MyScrapCourse>?
    suspend fun postMyScrapCourse(requestCourseScrap: RequestCourseScrap): Response<ResponseCourseScrap>
}