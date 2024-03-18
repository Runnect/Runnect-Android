package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.MyDrawCourseDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetMyDrawDetail(
    @SerialName("user")
    val user: User,
    @SerialName("course")
    val course: Course
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
        @SerialName("isNowUser")
        val isNowUser: Boolean,
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

    fun toMyDrawCourseDetail() = MyDrawCourseDetail(
        title = course.title,
        imgUrl = course.image,
        isNowUser = course.isNowUser,
        distance = course.distance,
        courseId = course.id,
        path = course.path,
        departureName = course.departure.name
    )
}