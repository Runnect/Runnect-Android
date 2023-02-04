package com.runnect.runnect.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetScrapDto(
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
        @SerialName("Scraps")
        val scraps: List<Scrap>,
        @SerialName("user")
        val user: User,
    ) {
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

        @Serializable
        data class User(
            @SerialName("machineId")
            val machineId: String,
        )
    }
}