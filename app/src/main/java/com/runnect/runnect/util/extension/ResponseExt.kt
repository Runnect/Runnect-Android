package com.runnect.runnect.util.extension

import com.runnect.runnect.data.dto.*
import com.runnect.runnect.data.dto.response.*

fun Record.toData(): HistoryInfoDTO {
    return HistoryInfoDTO(
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

fun PublicCourseUpload.toData(): UserUploadCourseDTO {
    return UserUploadCourseDTO(
        id = id, title = title, img = image, departure = departure.region + ' ' + departure.city
    )
}

fun PrivateCourse.toData(): CourseLoadInfoDTO {
    return CourseLoadInfoDTO(
        id = id,
        img = image,
        departure = departure.region + ' ' + departure.city,
        distance = distance.toString()
    )
}

fun ResponsePostLogin.toData(): LoginDTO {
    with(this.data) {
        return LoginDTO(
            status = status,
            accessToken = accessToken,
            refreshToken = refreshToken,
            email = email,
            type = type
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