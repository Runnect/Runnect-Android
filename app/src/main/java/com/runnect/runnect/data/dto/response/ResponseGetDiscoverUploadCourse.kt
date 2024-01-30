package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.DiscoverUploadCourse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetDiscoverUploadCourse(
    @SerialName("user")
    val user: User,
    @SerialName("courses")
    val courses: List<PrivateCourse>
) {
    @Serializable
    data class User(
        @SerialName("id")
        val id: Int
    )

    @Serializable
    data class PrivateCourse(
        @SerialName("id")
        val id: Int,
        @SerialName("image")
        val image: String,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("distance")
        val distance: Double,
        @SerialName("title")
        val title: String,
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

    fun toUploadCourses(): List<DiscoverUploadCourse> = courses.map { course ->
        DiscoverUploadCourse(
            id = course.id,
            imageUrl = course.image,
            departure = course.departure.region + ' ' + course.departure.city,
            distance = course.distance.toString()
        )
    }
}