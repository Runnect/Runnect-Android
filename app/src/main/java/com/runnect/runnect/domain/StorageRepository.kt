package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.MyDrawCourse
import com.runnect.runnect.data.dto.MyScrapCourse
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawDTO
import retrofit2.Response

interface StorageRepository {
    suspend fun getMyDrawCourse(): MutableList<MyDrawCourse>?
    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse) : Response<ResponsePutMyDrawDTO>
    suspend fun getMyScrapCourse(): MutableList<MyScrapCourse>?
}