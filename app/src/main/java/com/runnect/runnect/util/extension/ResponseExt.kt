package com.runnect.runnect.util.extension

import com.runnect.runnect.data.dto.RecordInfoDTO
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.response.PublicCourse
import com.runnect.runnect.data.dto.response.Record

fun Record.toData(): RecordInfoDTO {
    return RecordInfoDTO(
        id = id,
        img = image,
        title = title,
        location = departure.region + ' ' + departure.city,
        date = (createdAt.split(" ")[0]).replace("-", "."),
        distance = distance.toString(),
        time = timeConvert(time),
        pace = paceConvert(pace)
    )
}

fun PublicCourse.toData(): UserUploadCourseDTO {
    return UserUploadCourseDTO(
        id = id,
        title = title,
        img = image,
        departure = departure.region + ' ' + departure.city
    )
}

private fun timeConvert(time: String): String {
    var hms = mutableListOf<String>()
    hms = time.split(":").toMutableList()
    if (hms[0] == "00") {
        hms[0] = "0"
    }
    return "${hms[0]}:${hms[1]}:${hms[2]}"
}

private fun paceConvert(p: String): String {
    var pace = mutableListOf<String>()
    pace = p.split(":").toMutableList()
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