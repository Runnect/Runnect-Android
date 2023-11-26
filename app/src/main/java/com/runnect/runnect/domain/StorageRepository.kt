package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.MyDrawCourse
import com.runnect.runnect.data.dto.MyScrapCourse
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawCourse
import retrofit2.Response

interface StorageRepository {
    suspend fun getMyDrawCourse(): MutableList<MyDrawCourse>?
    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse) : Response<ResponsePutMyDrawCourse>
    suspend fun getMyScrapCourse(): MutableList<MyScrapCourse>?
}