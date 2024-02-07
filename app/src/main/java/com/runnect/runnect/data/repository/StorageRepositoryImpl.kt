package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.MyScrapCourse
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyScrapCourse
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawCourse
import com.runnect.runnect.data.dto.response.toMyDrawCourse
import com.runnect.runnect.data.source.remote.RemoteStorageDataSource
import com.runnect.runnect.domain.entity.MyDrawCourse
import com.runnect.runnect.domain.repository.StorageRepository
import retrofit2.Response
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(private val remoteStorageDataSource: RemoteStorageDataSource) :
    StorageRepository {
    override suspend fun getMyDrawCourse(): Result<List<MyDrawCourse>?> = runCatching{
        remoteStorageDataSource.getMyDrawCourse().data?.toMyDrawCourse()
    }

    override suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Response<ResponsePutMyDrawCourse> {
        return remoteStorageDataSource.deleteMyDrawCourse(deleteCourseList = deleteCourseList)
    }

    override suspend fun getMyScrapCourse(): MutableList<MyScrapCourse> {
        return changeMyScrapData(
            data = remoteStorageDataSource.getMyScrapCourse().body()!!.data.scraps
        ).toMutableList()
    }

    private fun changeMyScrapData(data: List<ResponseGetMyScrapCourse.Data.Scrap>): List<MyScrapCourse> {
        val changedData = data.map {
            MyScrapCourse(
                courseId = it.courseId,
                id = it.id,
                publicCourseId = it.publicCourseId,
                image = it.image,
                city = it.departure.city,
                region = it.departure.region,
                title = it.title
            )
        }
        return changedData
    }
}