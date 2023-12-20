package com.runnect.runnect.domain.entity

sealed class DiscoverMultiViewItem(
    open val id: Int
) {
    data class MarathonCourse(
        override val id: Int,
        val courseId: Int,
        var title: String,
        val image: String,
        var scrap: Boolean,
        val departure: String,
    ) : DiscoverMultiViewItem(id)

    data class RecommendHeader(
        val title: String,
        val subtitle: String
    ) : DiscoverMultiViewItem(HEADER_ID)

    data class RecommendCourse(
        override val id: Int,
        val courseId: Int,
        var title: String,
        val image: String,
        var scrap: Boolean,
        val departure: String,
    ) : DiscoverMultiViewItem(id)

    companion object {
        private const val HEADER_ID = -1
    }
}