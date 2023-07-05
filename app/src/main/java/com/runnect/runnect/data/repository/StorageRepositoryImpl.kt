package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.model.*
import com.runnect.runnect.data.source.remote.StorageDataSource
import com.runnect.runnect.domain.StorageRepository
import retrofit2.Response
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(private val storageDataSource: StorageDataSource) :
    StorageRepository {

    override suspend fun getMyDrawCourse(): MutableList<MyDrawCourse> {
        return changeMyDrawData(
            storageDataSource.getMyDrawCourse().body()!!.data.courses
        ).toMutableList()
    }

    private fun changeMyDrawData(data: List<ResponseGetCourseDto.Data.Course>): List<MyDrawCourse> {
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

    override suspend fun deleteMyDrawCourse(deleteCourseList: RequestPutMyDrawDto): Response<ResponsePutMyDrawDto> {
        return storageDataSource.deleteMyDrawCourse(deleteCourseList)
    }

    override suspend fun getMyScrapCourse(): MutableList<MyScrapCourse> {
        return changeMyScrapData(
            storageDataSource.getMyScrapCourse().body()!!.data.scraps
        ).toMutableList()
    }

    override suspend fun postMyScrapCourse(requestCourseScrap: RequestCourseScrap): Response<ResponseCourseScrap> {
        return storageDataSource.postMyScrapCourse(requestCourseScrap)
    }

    private fun changeMyScrapData(data: List<ResponseGetScrapDto.Data.Scrap>): List<MyScrapCourse> {
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