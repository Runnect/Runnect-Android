package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.MyCourseRanking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetMyCourseRanking(
    @SerialName("hasRecord")
    val hasRecord: Boolean,
    @SerialName("rank")
    val rank: Int? = null,
    @SerialName("userId")
    val userId: Long,
    @SerialName("nickname")
    val nickname: String? = null,
    @SerialName("recordId")
    val recordId: Long? = null,
    @SerialName("time")
    val time: String? = null,
    @SerialName("pace")
    val pace: String? = null,
) {
    fun toMyCourseRanking() = MyCourseRanking(
        hasRecord = hasRecord,
        rank = rank,
        userId = userId.toInt(),
        nickname = nickname,
        time = time,
        pace = pace,
    )
}
