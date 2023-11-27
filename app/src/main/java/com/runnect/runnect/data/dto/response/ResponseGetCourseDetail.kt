package com.runnect.runnect.data.dto.response


import com.runnect.runnect.domain.entity.CourseDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetCourseDetail(
    @SerialName("user")
    val user: User,
    @SerialName("publicCourse")
    val publicCourse: PublicCourse
) {
    @Serializable
    data class User(
        @SerialName("nickname")
        val nickname: String,
        @SerialName("level")
        val level: Int,
        @SerialName("image")
        val image: String,
        @SerialName("isNowUser")
        val isNowUser: Boolean
    )

    @Serializable
    data class PublicCourse(
        @SerialName("id")
        val id: Int,
        @SerialName("courseId")
        val courseId: Int,
        @SerialName("scrap")
        val scrap: Boolean,
        @SerialName("scrapCount")
        val scrapCount: Long,
        @SerialName("image")
        val image: String,
        @SerialName("title")
        val title: String,
        @SerialName("description")
        val description: String,
        @SerialName("path")
        val path: List<List<Double>>,
        @SerialName("distance")
        val distance: Double,
        @SerialName("departure")
        val departure: Departure
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
            val name: String?
        )
    }

    fun toCourseDetail() = CourseDetail(
        stampId = user.image,
        level = user.level.toString(),
        nickname = user.nickname,
        isNowUser = user.isNowUser,
        id = publicCourse.id,
        courseId = publicCourse.courseId,
        departure = publicCourse.departure.region + ' ' +
                publicCourse.departure.city + ' ' +
                publicCourse.departure.town + ' ' +
                ((publicCourse.departure.name) ?: ""),
        description = publicCourse.description,
        distance = publicCourse.distance.toString(),
        image = publicCourse.image,
        scrap = publicCourse.scrap,
        scrapCount = publicCourse.scrapCount.toString(),
        title = publicCourse.title,
        path = publicCourse.path
    )
}