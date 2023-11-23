package com.runnect.runnect.data.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RequestDeleteUploadCourse(
    @SerializedName("publicCourseIdList")
    val publicCourseIdList: List<Int>
)
