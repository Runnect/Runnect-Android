package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.MyDrawCourse
import com.runnect.runnect.data.dto.MyScrapCourse
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetCourseDTO
import com.runnect.runnect.data.dto.response.ResponseGetScrapDTO
import com.runnect.runnect.data.dto.response.ResponsePutMyDrawDTO
import com.runnect.runnect.data.source.remote.RemoteStorageDataSource
import com.runnect.runnect.domain.StorageRepository
import retrofit2.Response
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(private val remoteStorageDataSource: RemoteStorageDataSource) :
    StorageRepository {
    override suspend fun getMyDrawCourse(): MutableList<MyDrawCourse> {
        return changeMyDrawData(
            data = remoteStorageDataSource.getMyDrawCourse().body()!!.data.courses
        ).toMutableList()
    }

    private fun changeMyDrawData(data: List<ResponseGetCourseDTO.Data.Course>): List<MyDrawCourse> {
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

    override suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawCourse): Response<ResponsePutMyDrawDTO> {
        return remoteStorageDataSource.deleteMyDrawCourse(deleteCourseList = deleteCourseList)
    }

    override suspend fun getMyScrapCourse(): MutableList<MyScrapCourse> {
        return changeMyScrapData(
            data = remoteStorageDataSource.getMyScrapCourse().body()!!.data.scraps
        ).toMutableList()
    }

    private fun changeMyScrapData(data: List<ResponseGetScrapDTO.Data.Scrap>): List<MyScrapCourse> {
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