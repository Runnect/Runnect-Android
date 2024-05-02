package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import com.runnect.runnect.domain.entity.UserUploadCourse
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetUserUploadCourse(
    @SerializedName("user")
    val user: UserId,
    @SerializedName("publicCourses")
    val publicCourses: List<PublicCourseUpload>,
) {

    @Serializable
    data class UserId(
        @SerializedName("id")
        val id: Int,
    )

    @Serializable
    data class PublicCourseUpload(
        @SerializedName("id")
        val id: Int,
        @SerializedName("courseId")
        val courseId: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("scrap")
        val scrap: Boolean,
        @SerializedName("image")
        val image: String,
        @SerializedName("departure")
        val departure: DepartureUpload,
    )

    @Serializable
    data class DepartureUpload(
        @SerializedName("region")
        val region: String,
        @SerializedName("city")
        val city: String,
    )

    fun toUserUploadCourse(): List<UserUploadCourse> = publicCourses.map {
        UserUploadCourse(
            id = it.id,
            title = it.title,
            img = it.image,
            departure = "${it.departure.region} ${it.departure.city}"
        )
    }
}