package com.runnect.runnect.domain.entity

sealed class DiscoverScrollItem {
    data class Banner(
        val index: Int,
        val imageUrl: String,
        val linkUrl: String
    ) : DiscoverScrollItem()

    data class MarathonCourse(
        val id: Int,
        val courseId: Int,
        var title: String,
        val image: String,
        var scrap: Boolean,
        val departure: String,
    ) : DiscoverScrollItem()

    data class RecommendCourse(
        val id: Int,
        val courseId: Int,
        var title: String,
        val image: String,
        var scrap: Boolean,
        val departure: String,
    ) : DiscoverScrollItem()
}