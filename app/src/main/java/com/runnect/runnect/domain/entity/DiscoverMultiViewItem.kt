package com.runnect.runnect.domain.entity

import com.runnect.runnect.presentation.discover.adapter.multiview.DiscoverMultiViewType
import com.runnect.runnect.presentation.discover.adapter.multiview.DiscoverMultiViewType.*

sealed class DiscoverMultiViewItem(
    private val viewType: DiscoverMultiViewType,
    open val id: Int
) {
    data class MarathonCourse(
        override val id: Int,
        val courseId: Int,
        var title: String,
        val image: String,
        var scrap: Boolean,
        val departure: String,
    ) : DiscoverMultiViewItem(MARATHON, id)

    data class RecommendHeader(
        override val id: Int,
        val title: String,
        val subtitle: String
    ): DiscoverMultiViewItem(RECOMMEND_HEADER, id)

    data class RecommendCourse(
        override val id: Int,
        val courseId: Int,
        var title: String,
        val image: String,
        var scrap: Boolean,
        val departure: String,
    ) : DiscoverMultiViewItem(RECOMMEND_COURSE, id)

    fun getMultiViewType() = viewType
}