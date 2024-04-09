package com.runnect.runnect.domain.repository

import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawCourse
import com.runnect.runnect.domain.entity.MyDrawCourse
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    suspend fun getMyDrawCourse(): Flow<Result<List<MyDrawCourse>>>
    suspend fun getMyScrapCourse(): Flow<Result<List<MyScrapCourse>>>
    suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse) : Flow<Result<ResponsePutMyDrawCourse>>
}