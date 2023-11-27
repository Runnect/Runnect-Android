package com.runnect.runnect.data.dto.response
import com.runnect.runnect.domain.entity.DiscoverCourse
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetDiscoverSearch(
    val publicCourses: List<PublicCourse>
) {
    @Serializable
    data class PublicCourse(
        val id: Int,
        val courseId: Int,
        val title: String,
        val image: String,
        val scrap: Boolean,
        val departure: Departure,
    )

    @Serializable
    data class Departure(
        val city: String,
        val region: String
    )

    fun toDiscoverCourses(): List<DiscoverCourse> = publicCourses.map { course ->
        DiscoverCourse(
            id = course.id,
            courseId = course.courseId,
            title = course.title,
            image = course.image,
            scrap = course.scrap,
            departure = "${course.departure.region} ${course.departure.city}"
        )
    }
}
