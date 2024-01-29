package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.MyDrawCourse
import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawCourse
import com.runnect.runnect.data.source.remote.RemoteStorageDataSource
import com.runnect.runnect.domain.repository.StorageRepository
import retrofit2.Response
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(private val remoteStorageDataSource: RemoteStorageDataSource) :
    StorageRepository {
    override suspend fun getMyDrawCourse(): MutableList<MyDrawCourse> {
        return changeMyDrawData(
            data = remoteStorageDataSource.getMyDrawCourse().body()!!.data.courses
        ).toMutableList()
    }

    private fun changeMyDrawData(data: List<ResponseGetMyDrawCourse.Data.Course>): List<MyDrawCourse> {
        val changedData = data.map {
            MyDrawCourse(
                courseId = it.id,
                image = it.image,
                city = it.departure.city,
                region = it.departure.region
            )
        }
        return changedData
    }

    override suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Response<ResponsePutMyDrawCourse> {
        return remoteStorageDataSource.deleteMyDrawCourse(deleteCourseList = deleteCourseList)
    }

    override suspend fun getMyScrapCourse(): Result<List<MyScrapCourse>?> =
        runCatching {
            remoteStorageDataSource.getMyScrapCourse().data?.toMyScrapCourses()
        }
}