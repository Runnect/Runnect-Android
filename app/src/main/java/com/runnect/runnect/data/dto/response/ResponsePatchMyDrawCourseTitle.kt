package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.EditableMyDrawCourseDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePatchMyDrawCourseTitle(
    @SerialName("course")
    val course: Course
) {
    @Serializable
    data class Course(
        @SerialName("id")
        val id: Int,
        @SerialName("title")
        val title: String
    )

    fun toEditableMyDrawCourseDetail() = EditableMyDrawCourseDetail(
        title = course.title
    )
}
