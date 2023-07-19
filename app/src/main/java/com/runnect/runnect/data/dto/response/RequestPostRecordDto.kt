package com.runnect.runnect.data.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPostRecordDto(
    @SerialName("courseId")
    val courseId: Int,
    @SerialName("publicCourseId")
    val publicCourseId: Int?,
    @SerialName("title")
    val title: String,
    @SerialName("time")
    val time: String,
    @SerialName("pace")
    val pace: String,
)