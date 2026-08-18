package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.CourseRanking
import com.runnect.runnect.domain.entity.CourseRankingEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetCourseRanking(
    @SerialName("totalCount")
    val totalCount: Long,
    @SerialName("entries")
    val entries: List<Entry>
) {
    @Serializable
    data class Entry(
        @SerialName("rank")
        val rank: Int,
        @SerialName("userId")
        val userId: Long,
        @SerialName("nickname")
        val nickname: String,
        @SerialName("recordId")
        val recordId: Long,
        @SerialName("time")
        val time: String,
        @SerialName("pace")
        val pace: String,
    )

    fun toCourseRanking() = CourseRanking(
        totalCount = totalCount.toInt(),
        entries = entries.map {
            CourseRankingEntry(
                rank = it.rank,
                userId = it.userId.toInt(),
                nickname = it.nickname,
                recordId = it.recordId.toInt(),
                time = it.time,
                pace = it.pace,
            )
        }
    )
}
