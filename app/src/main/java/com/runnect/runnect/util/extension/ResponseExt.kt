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

fun RecommendPublicCourse.toData(): RecommendCourseDTO {
    return RecommendCourseDTO(
        pageNo = pageNo,
        courseId = courseId,
        departure = departure.region + ' ' + departure.city,
        id = id,
        title = title,
        scrap = scrap,
        image = image
    )
}

fun SearchPublicCourse.toData(): CourseSearchDTO {
    return CourseSearchDTO(
        courseId = courseId,
        departure = departure.region + ' ' + departure.city,
        id = id,
        title = title,
        scrap = scrap,
        image = image
    )
}

//fun DetailData.toData(): CourseDetailDTO {
//    return CourseDetailDTO(
//        stampId = user.image,
//        level = user.level,
//        nickname = user.nickname,
//        courseId = publicCourse.courseId,
//        departure = publicCourse.departure.region + ' ' + publicCourse.departure.city + ' ' + publicCourse.departure.town + ' ' + ((publicCourse.departure.name)
//            ?: ""),
//        description = publicCourse.description,
//        distance = publicCourse.distance.toString(),
//        id = publicCourse.id,
//        image = publicCourse.image,
//        scrap = publicCourse.scrap,
//        title = publicCourse.title,
//        path = publicCourse.path
//    )
//}

fun PrivateCourse.toData(): CourseLoadInfoDTO {
    return CourseLoadInfoDTO(
        id = id,
        img = image,
        departure = departure.region + ' ' + departure.city,
        distance = distance.toString()
    )
}

fun ResponseLogin.toData(): LoginDTO {
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