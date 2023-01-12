package com.example.runnect.presentation.storage.api.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetCourseDto(
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
            @SerialName("machineId")
            val machineId: String,
        )
    }

}