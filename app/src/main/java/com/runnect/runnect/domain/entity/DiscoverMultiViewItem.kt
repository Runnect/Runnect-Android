package com.runnect.runnect.domain.entity

sealed class DiscoverMultiViewItem(
    open val id: Int
) {
    data class MarathonCourse(
        override val id: Int,
        val courseId: Int,
        val title: String,
        val image: String,
        val scrap: Boolean,
        val departure: String,
    ) : DiscoverMultiViewItem(id)

    data class RecommendCourse(
        override val id: Int,
        val courseId: Int,
        val title: String,
        val image: String,
        val scrap: Boolean,
        val departure: String,
    ) : DiscoverMultiViewItem(id)
}