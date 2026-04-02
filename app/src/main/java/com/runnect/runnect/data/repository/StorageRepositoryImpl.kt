package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.toMyDrawCourse
import com.runnect.runnect.data.network.mapToFlowResult
import com.runnect.runnect.data.source.remote.RemoteStorageDataSource
import com.runnect.runnect.domain.entity.MyDrawCourse
import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val remoteStorageDataSource: RemoteStorageDataSource
) : StorageRepository {

    override suspend fun getMyDrawCourse(): Flow<Result<List<MyDrawCourse>>> =
        remoteStorageDataSource.getMyDrawCourse().mapToFlowResult { it.toMyDrawCourse() }

    override suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Flow<Result<Unit>> {
        return remoteStorageDataSource.deleteMyDrawCourse(deleteCourseList = deleteCourseList).mapToFlowResult {}
    }

    override suspend fun getMyScrapCourse(): Flow<Result<List<MyScrapCourse>>> =
        remoteStorageDataSource.getMyScrapCourse().mapToFlowResult { it.toMyScrapCourses() }
}