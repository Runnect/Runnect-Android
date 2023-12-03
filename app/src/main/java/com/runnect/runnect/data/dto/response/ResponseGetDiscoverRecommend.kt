package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.DiscoverCourse
import com.runnect.runnect.domain.entity.DiscoverMultiItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetDiscoverRecommend(
    @SerialName("ordering")
    val ordering: String,
    @SerialName("publicCourses")
    val publicCourses: List<PublicCourse>
) {
    @Serializable
    data class PublicCourse(
        @SerialName("id")
        val id: Int,
        @SerialName("courseId")
        val courseId: Int,
        @SerialName("title")
        val title: String,
        @SerialName("image")
        val image: String,
        @SerialName("scrap")
        val scrap: Boolean,
        @SerialName("departure")
        val departure: Departure
    ) {
        @Serializable
        data class Departure(
            @SerialName("city")
            val city: String,
            @SerialName("region")
            val region: String
        )
    }

    fun toDiscoverCourses(): List<DiscoverMultiItem.RecommendCourse> = publicCourses.map { course ->
        DiscoverMultiItem.RecommendCourse(
            id = course.id,
            courseId = course.courseId,
            title = course.title,
            image = course.image,
            scrap = course.scrap,
            departure = "${course.departure.region} ${course.departure.city}"
        )
    }
}
