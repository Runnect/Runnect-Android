package com.runnect.runnect.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMyCourseLoad(
    @SerializedName("data")
    val `data`: LoadData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

@Serializable
data class LoadUser(
    @SerializedName("id")
    val id: Int
)

@Serializable
data class LoadDeparture(
    @SerializedName("city")
    val city: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("region")
    val region: String,
    @SerializedName("town")
    val town: String
)

@Serializable
data class PrivateCourse(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("departure")
    val departure: LoadDeparture,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String
)

@Serializable
data class LoadData(
    @SerializedName("privateCourses")
    val privateCourses: List<PrivateCourse>,
    @SerializedName("user")
    val user: LoadUser
)