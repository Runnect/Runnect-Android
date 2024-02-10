package com.runnect.runnect.domain.repository

import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawCourse
import com.runnect.runnect.domain.entity.MyDrawCourse
import retrofit2.Response

interface StorageRepository {
    suspend fun getMyDrawCourse(): Result<List<MyDrawCourse>?>
    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse) : Response<ResponsePutMyDrawCourse>
    suspend fun getMyScrapCourse(): Result<List<MyScrapCourse>?>
}