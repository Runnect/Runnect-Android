package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.MyDrawCourse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetMyDrawCourse(
    @SerialName("data")
    val `data`: Data,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Int,
    @SerialName("success")
    val success: Boolean,
) {
    @Serializable
    data class Data(
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
}

fun List<ResponseGetMyDrawCourse.Data.Course>.toMyDrawCourse(): List<MyDrawCourse> {
    return this.map {
        MyDrawCourse(
            courseId = it.id,
            image = it.image,
            city = it.departure.city,
            region = it.departure.region,
            title = it.title
        )
    }
}