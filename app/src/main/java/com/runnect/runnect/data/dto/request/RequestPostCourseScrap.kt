package com.runnect.runnect.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPostCourseScrap(
    @SerialName("publicCourseId")
    val publicCourseId: Int,
    @SerialName("scrapTF")
    val scrapTF: String
)
