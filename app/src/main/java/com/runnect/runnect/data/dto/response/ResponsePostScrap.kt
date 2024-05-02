package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.PostScrap
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
) {
    fun toPostScrap(): PostScrap {
        return PostScrap(
            publicCourseId = publicCourseId,
            scrapCount = scrapCount,
            scrapTF = scrapTF
        )
    }
}