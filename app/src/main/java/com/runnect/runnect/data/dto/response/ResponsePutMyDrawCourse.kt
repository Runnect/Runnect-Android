package com.runnect.runnect.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePutMyDrawCourse(
    @SerialName("deletedCourseCount")
    val deletedCourseCount: Int
)