package com.runnect.runnect.data.dto.response
import kotlinx.serialization.Serializable

@Serializable
data class ResponseRecordInfo(
    val `data`: HistoryData,
    val message: String,
    val status: Int,
    val success: Boolean
)
@Serializable
data class Record(
    val courseId: Int,
    val createdAt: String,
    val departure: Departure,
    val distance: Double,
    val id: Int,
    val image: String,
    val machineId: String,
    val pace: String,
    val publicCourseId: Int?,
    val time: String,
    val title: String
)
@Serializable
data class Departure(
    val city: String,
    val region: String
)
@Serializable
data class RecordUser(
    val machineId: String
)
@Serializable
data class HistoryData(
    val records: List<Record>,
    val user: RecordUser
)

