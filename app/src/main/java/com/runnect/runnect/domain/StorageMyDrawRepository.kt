package com.runnect.runnect.domain

import com.runnect.runnect.data.model.MyDrawCourse
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.SearchResultEntity

interface StorageMyDrawRepository {
    suspend fun getMyDrawCourseList(): List<MyDrawCourse>?
}