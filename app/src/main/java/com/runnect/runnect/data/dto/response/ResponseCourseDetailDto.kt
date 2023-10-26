package com.runnect.runnect.data.dto.response


import com.runnect.runnect.domain.entity.CourseDetail
import kotlinx.serialization.Serializable

@Serializable
data class ResponseCourseDetailDto(
    val user: User,
    val publicCourse: PublicCourse
) {
    @Serializable
    data class User(
        val nickname: String,
        val level: Int,
        val image: String,
        val isNowUser: Boolean
    )

    @Serializable
    data class PublicCourse(
        val id: Int,
        val courseId: Int,
        val isScrap: Boolean,
        val scrapCount: Long,
        val image: String,
        val title: String,
        val description: String,
        val path: List<List<Double>>,
        val distance: Double,
        val departure: Departure
    ) {
        @Serializable
        data class Departure(
            val region: String,
            val city: String,
            val town: String,
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
        isScrap = publicCourse.isScrap,
        title = publicCourse.title,
        path = publicCourse.path
    )
}