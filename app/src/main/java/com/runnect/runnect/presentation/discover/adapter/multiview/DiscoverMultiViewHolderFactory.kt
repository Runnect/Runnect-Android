package com.runnect.runnect.presentation.discover.adapter.multiview

import android.view.ViewGroup
import com.runnect.runnect.R
import com.runnect.runnect.presentation.discover.adapter.DiscoverMarathonAdapter
import com.runnect.runnect.presentation.discover.adapter.DiscoverRecommendAdapter
import com.runnect.runnect.util.extension.getViewDataBinding

class DiscoverMultiViewHolderFactory {
    lateinit var marathonCourseAdapter: DiscoverMarathonAdapter
    lateinit var recommendCourseAdapter: DiscoverRecommendAdapter

    fun createMultiViewHolder(
        parent: ViewGroup,
        viewType: Int,
        onHeartButtonClick: (Int, Boolean) -> Unit,
        onCourseItemClick: (Int) -> Unit,
        handleVisitorMode: () -> Unit,
        onSortButtonClick: (String) -> Unit
    ): DiscoverMultiViewHolder {
        when (viewType) {
            DiscoverMultiViewType.MARATHON.ordinal -> {
                val viewHolder = DiscoverMultiViewHolder.MarathonCourseViewHolder(
                    binding = parent.getViewDataBinding(layoutRes = R.layout.item_discover_multiview_marathon),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick,
                    handleVisitorMode = handleVisitorMode
                )
                marathonCourseAdapter = viewHolder.marathonAdapter
                return viewHolder
            }

            else -> {
                val viewHolder = DiscoverMultiViewHolder.RecommendCourseViewHolder(
                    binding = parent.getViewDataBinding(layoutRes = R.layout.item_discover_multiview_recommend),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick,
                    handleVisitorMode = handleVisitorMode,
                    onSortButtonClick = onSortButtonClick
                )
                recommendCourseAdapter = viewHolder.recommendAdapter
                return viewHolder
            }
        }
    }
}