package com.runnect.runnect.data.dto.response


import com.runnect.runnect.domain.entity.MyDrawCourse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetMyDrawDetail(
    @SerialName("data")
    val data: Data,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Int,
    @SerialName("success")
    val success: Boolean,
) {
    @Serializable
    data class Data(
        @SerialName("course")
        val course: Course,
        @SerialName("user")
        val user: User,
    ) {
        @Serializable
        data class User(
            @SerialName("userId")
            val id: Int,
        )

        @Serializable
        data class Course(
            @SerialName("id")
            val id: Int,
            @SerialName("createdAt")
            val createdAt: String,
            @SerialName("path")
            val path: List<List<Double>>,
            @SerialName("distance")
            val distance: Float,
            @SerialName("image")
            val image: String,
            @SerialName("title")
            val title: String,
            @SerialName("departure")
            val departure: Departure,
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
                val name: String,
            )
        }
    }
}

fun ResponseGetMyDrawDetail.toMyDrawCourse(): MyDrawCourse {
    this.data.course.apply {
        return MyDrawCourse(
            courseId = id,
            image = image,
            city = this.departure.city,
            region = this.departure.region,
            title = title
        )
    }
}