package com.runnect.runnect.data.dto.response
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetDiscoverSearch(
    val `data`: SearchData,
    val message: String,
    val status: Int,
    val success: Boolean
)

@Serializable
data class SearchData(
    val publicCourses: List<SearchPublicCourse>
)

@Serializable
data class SearchDeparture(
    val city: String,
    val region: String
)

@Serializable
data class SearchPublicCourse(
    val courseId: Int,
    val departure: SearchDeparture,
    val id: Int,
    val image: String,
    val scrap: Boolean,
    val title: String
)