package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.MyDrawCourse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetMyDrawCourse(
    @SerialName("courses")
    val courses: List<Course>,
    @SerialName("user")
    val user: User,
) {
    @Serializable
    data class Course(
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("departure")
        val departure: Departure,
        @SerialName("id")
        val id: Int,
        @SerialName("image")
        val image: String,
        @SerialName("title")
        val title: String
    ) {
        @Serializable
        data class Departure(
            @SerialName("city")
            val city: String,
            @SerialName("region")
            val region: String,
        )
    }

    @Serializable
    data class User(
        @SerialName("id")
        val id: Int,
    )
}

fun ResponseGetMyDrawCourse.toMyDrawCourse(): List<MyDrawCourse> {
    return this.courses.map {
        MyDrawCourse(
            courseId = it.id,
            image = it.image,
            city = it.departure.city,
            region = it.departure.region,
            title = it.title
        )
    }
}