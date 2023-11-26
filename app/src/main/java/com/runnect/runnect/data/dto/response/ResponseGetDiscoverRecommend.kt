package com.runnect.runnect.data.dto.response
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetDiscoverRecommend(
    val `data`: RecommendData,
    val message: String,
    val status: Int,
    val success: Boolean
)

@Serializable
data class RecommendPublicCourse(
    val pageNo: Int,
    val courseId: Int,
    val departure: RecommendDeparture,
    val id: Int,
    val image: String,
    val scrap: Boolean,
    val title: String
)

@Serializable
data class RecommendDeparture(
    val city: String,
    val region: String
)

@Serializable
data class RecommendData(
    val publicCourses: List<RecommendPublicCourse>
)