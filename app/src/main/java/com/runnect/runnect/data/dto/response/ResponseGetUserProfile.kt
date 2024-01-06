package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.Departure
import com.runnect.runnect.domain.entity.UserCourse
import com.runnect.runnect.domain.entity.UserProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetUserProfile(
    @SerialName("user")
    val user: User,
    @SerialName("courses")
    val courses: List<CourseData>
) {
    @Serializable
    data class User(
        @SerialName("nickname")
        val nickname: String,
        @SerialName("level")
        val level: Int,
        @SerialName("levelPercent")
        val levelPercent: Int,
        @SerialName("latestStamp")
        val latestStamp: String,
        @SerialName("userId")
        val userId: Int
    )

    @Serializable
    data class CourseData(
        @SerialName("publicCourseId")
        val publicCourseId: Int,
        @SerialName("courseId")
        val courseId: Int,
        @SerialName("title")
        val title: String,
        @SerialName("image")
        val image: String,
        @SerialName("departure")
        val departure: Departure,
        @SerialName("scrapTF")
        val scrapTF: Boolean,
    ) {
        @Serializable
        data class Departure(
            @SerialName("region")
            val region: String,
            @SerialName("city")
            val city: String,
            @SerialName("town")
            val town: String,
            @SerialName("name")
            val name: String?,
            @SerialName("detail")
            val detail: String?
        )
    }

    fun toUserProfile(): UserProfile {
        val userCourseLists: List<UserCourse> = courses.map { course ->
            UserCourse(
                publicCourseId = course.publicCourseId,
                courseId = course.courseId,
                title = course.title,
                image = course.image,
                departure = Departure(
                    region = course.departure.region,
                    city = course.departure.city,
                    town = course.departure.town,
                    detail = course.departure.detail,
                    name = course.departure.name ?: ""
                ),
                scrapTF = course.scrapTF
            )
        }

        return UserProfile(
            nickname = user.nickname,
            level = user.level,
            levelPercent = user.levelPercent,
            latestStamp = user.latestStamp,
            courseData = userCourseLists
        )
    }

}
