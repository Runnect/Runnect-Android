package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
@Serializable
data class ResponseDeleteUploadCourse(
    @SerializedName("deletedPublicCourseCount")
    val deletedPublicCourseCount: Int
)
