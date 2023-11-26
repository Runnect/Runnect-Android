package com.runnect.runnect.data.dto.response


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
        data class Course(
            @SerialName("createdAt")
            val createdAt: String,
            @SerialName("departure")
            val departure: Departure,
            @SerialName("distance")
            val distance: Float,
            @SerialName("id")
            val id: Int,
            @SerialName("image")
            val image: String,
            @SerialName("path")
            val path: List<List<Double>>,
        ) {
            @Serializable
            data class Departure(
                @SerialName("city")
                val city: String,
                @SerialName("name")
                val name: String,
                @SerialName("region")
                val region: String,
                @SerialName("town")
                val town: String,
            )
        }

        @Serializable
        data class User(
            @SerialName("id")
            val id: Int,
        )
    }
}