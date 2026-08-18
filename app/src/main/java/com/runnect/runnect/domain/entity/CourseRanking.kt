package com.runnect.runnect.domain.entity

data class CourseRankingEntry(
    val rank: Int,
    val userId: Int,
    val nickname: String,
    val recordId: Int,
    val time: String,
    val pace: String,
)

data class CourseRanking(
    val totalCount: Int,
    val entries: List<CourseRankingEntry>,
)

data class MyCourseRanking(
    val hasRecord: Boolean,
    val rank: Int?,
    val userId: Int,
    val nickname: String?,
    val time: String?,
    val pace: String?,
)
