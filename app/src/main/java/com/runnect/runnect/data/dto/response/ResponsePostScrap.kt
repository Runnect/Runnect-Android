package com.runnect.runnect.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePostScrap(
    @SerialName("publicCourseId")
    val publicCourseId: Long,
    @SerialName("scrapCount")
    val scrapCount: Long,
    @SerialName("scrapTF")
    val scrapTF: Boolean
)