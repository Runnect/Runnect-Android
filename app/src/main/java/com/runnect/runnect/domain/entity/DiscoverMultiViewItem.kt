package com.runnect.runnect.domain.entity

import com.runnect.runnect.presentation.discover.adapter.multiview.DiscoverMultiViewType
import com.runnect.runnect.presentation.discover.adapter.multiview.DiscoverMultiViewType.*

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

    data class RecommendCourse(
        override val id: Int,
        val courseId: Int,
        var title: String,
        val image: String,
        var scrap: Boolean,
        val departure: String,
    ) : DiscoverMultiViewItem(id)
}