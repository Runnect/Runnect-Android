package com.runnect.runnect.data.dto.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPutMyDrawCourse(
    @SerialName("courseIdList")
    val courseIdList: List<Int>
)