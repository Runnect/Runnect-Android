package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.MyScrapCourse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetMyScrapCourse(
    @SerialName("user")
    val user: User,
    @SerialName("scraps")
    val scraps: List<Scrap>,
) {
    @Serializable
    data class User(
        @SerialName("userId")
        val id: Int,
    )

    @Serializable
    data class Scrap(
        @SerialName("courseId")
        val courseId: Int,
        @SerialName("departure")
        val departure: Departure,
        @SerialName("id")
        val id: Int,
        @SerialName("image")
        val image: String,
        @SerialName("publicCourseId")
        val publicCourseId: Int,
        @SerialName("title")
        val title: String,
    ) {
        @Serializable
        data class Departure(
            @SerialName("city")
            val city: String,
            @SerialName("region")
            val region: String,
        )
    }

    fun toMyScrapCourses(): List<MyScrapCourse> = scraps.map { course ->
        MyScrapCourse(
            courseId = course.courseId,
            id = course.id,
            publicCourseId = course.publicCourseId,
            image = course.image,
            city = course.departure.city,
            region = course.departure.region,
            title = course.title
        )
    }
}