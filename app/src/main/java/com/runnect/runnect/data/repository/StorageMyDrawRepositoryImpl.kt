package com.runnect.runnect.data.repository

import com.runnect.runnect.data.model.MyDrawCourse
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.source.remote.StorageMyDrawDataSource
import com.runnect.runnect.domain.StorageMyDrawRepository

class StorageMyDrawRepositoryImpl(private val storageMyDrawDataSource: StorageMyDrawDataSource) :
    StorageMyDrawRepository {

    override suspend fun getMyDrawCourseList(): List<MyDrawCourse> {

        return changeData(storageMyDrawDataSource.getMyDrawCourseList().body()!!.data.courses)
    }

    private fun changeData(data: List<ResponseGetCourseDto.Data.Course>): List<MyDrawCourse> {
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
}