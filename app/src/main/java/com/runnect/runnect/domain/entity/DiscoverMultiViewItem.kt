package com.runnect.runnect.domain.entity

sealed class DiscoverMultiViewItem {
    data class MarathonCourse(
        val id: Int,
        val courseId: Int,
        var title: String,
        val image: String,
        var scrap: Boolean,
        val departure: String,
    ) : DiscoverMultiViewItem()

    data class RecommendCourse(
        val id: Int,
        val courseId: Int,
        var title: String,
        val image: String,
        var scrap: Boolean,
        val departure: String,
    ) : DiscoverMultiViewItem()
}