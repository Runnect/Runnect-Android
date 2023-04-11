package com.runnect.runnect.util.extension

import com.runnect.runnect.R
import com.runnect.runnect.data.dto.*
import com.runnect.runnect.data.dto.response.*

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
        id = id, title = title, img = image, departure = departure.region + ' ' + departure.city
    )
}

fun RecommendPublicCourse.toData(): RecommendCourseDTO {
    return RecommendCourseDTO(
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

fun DetailData.toData(): CourseDetailDTO {
    return CourseDetailDTO(
        stampId = getProfileStamp(user.image),
        level = user.level.toString(),
        nickname = user.nickname,
        courseId = publicCourse.courseId,
        departure = publicCourse.departure.region + ' ' + publicCourse.departure.city + ' ' + publicCourse.departure.town + ' ' + ((publicCourse.departure.name)
            ?: ""),
        description = publicCourse.description,
        distance = publicCourse.distance.toString(),
        id = publicCourse.id,
        image = publicCourse.image,
        scrap = publicCourse.scrap,
        title = publicCourse.title,
        path = publicCourse.path
    )
}

fun PrivateCourse.toData(): CourseLoadInfoDTO {
    return CourseLoadInfoDTO(
        id = id,
        img = image,
        departure = departure.region + ' ' + departure.city + ' ' + departure.town + ' ' + departure.name,
        distance = distance.toString()
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

private fun getProfileStamp(stamp: String): Int {
    return when (stamp) {
        "c1" -> R.drawable.mypage_img_stamp_c1
        "c2" -> R.drawable.mypage_img_stamp_c2
        "c3" -> R.drawable.mypage_img_stamp_c3
        "s1" -> R.drawable.mypage_img_stamp_s1
        "s2" -> R.drawable.mypage_img_stamp_s2
        "s3" -> R.drawable.mypage_img_stamp_s3
        "u1" -> R.drawable.mypage_img_stamp_u1
        "u2" -> R.drawable.mypage_img_stamp_u2
        "u3" -> R.drawable.mypage_img_stamp_u3
        "r1" -> R.drawable.mypage_img_stamp_r1
        "r2" -> R.drawable.mypage_img_stamp_r2
        "r3" -> R.drawable.mypage_img_stamp_r3
        else -> R.drawable.user_profile_basic
    }
}