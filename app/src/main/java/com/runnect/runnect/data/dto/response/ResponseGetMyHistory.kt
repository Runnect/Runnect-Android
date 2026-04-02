package com.runnect.runnect.data.dto.response

import com.runnect.runnect.data.dto.HistoryInfoDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetMyHistory(
    val user: RecordUser,
    val records: List<Record>,
) {
    @Serializable
    data class RecordUser(
        @SerialName("userId") val id: Int
    )

    @Serializable
    data class Record(
        val courseId: Int,
        val createdAt: String,
        val departure: Departure,
        val distance: Double,
        val id: Int,
        val image: String,
        val pace: String,
        val publicCourseId: Int?,
        val time: String,
        val title: String
    ) {
        @Serializable
        data class Departure(
            val city: String,
            val region: String
        )
    }

    fun toHistoryInfoList(): List<HistoryInfoDTO> {
        return records.map {
            HistoryInfoDTO(
                id = it.id,
                img = it.image,
                title = it.title,
                location = "${it.departure.region} ${it.departure.city}",
                date = (it.createdAt.split(" ")[0]).replace("-", "."),
                distance = it.distance.toString(),
                time = timeConvert(it.time),
                pace = paceConvert(it.pace)
            )
        }
    }

    private fun timeConvert(time: String): String {
        val hms = time.split(":").toMutableList()
        if (hms[0] == "00") {
            hms[0] = "0"
        }
        return "${hms[0]}:${hms[1]}:${hms[2]}"
    }

    private fun paceConvert(p: String): String {
        val pace = p.split(":").toMutableList()
        return if (pace[0] == "00") {
            pace.removeAt(0)
            if (pace[0][0] == '0') {
                pace[0] = pace[0][1].toString()
            }
            "${pace[0]}’${pace[1]}”"
        } else {
            "${pace[0]}’${pace[1]}”${pace[2]}”"
        }
    }
}
