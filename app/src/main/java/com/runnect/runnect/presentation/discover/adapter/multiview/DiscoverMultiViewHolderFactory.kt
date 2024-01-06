package com.runnect.runnect.presentation.discover.adapter.multiview

import android.view.ViewGroup
import com.runnect.runnect.R
import com.runnect.runnect.util.extension.getViewDataBinding

class DiscoverMultiViewHolderFactory {
    lateinit var marathonViewHolder: DiscoverMultiViewHolder.MarathonCourseViewHolder
    lateinit var recommendViewHolder: DiscoverMultiViewHolder.RecommendCourseViewHolder

    fun createMultiViewHolder(
        parent: ViewGroup,
        viewType: DiscoverMultiViewType,
        onHeartButtonClick: (Int, Boolean) -> Unit,
        onCourseItemClick: (Int) -> Unit,
        handleVisitorMode: () -> Unit,
        onSortButtonClick: (String) -> Unit
    ): DiscoverMultiViewHolder {
        when (viewType) {
            DiscoverMultiViewType.MARATHON -> {
                marathonViewHolder = DiscoverMultiViewHolder.MarathonCourseViewHolder(
                    binding = parent.getViewDataBinding(layoutRes = R.layout.item_discover_multiview_marathon),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick,
                    handleVisitorMode = handleVisitorMode
                )
                return marathonViewHolder
            }

            DiscoverMultiViewType.RECOMMEND -> {
                recommendViewHolder = DiscoverMultiViewHolder.RecommendCourseViewHolder(
                    binding = parent.getViewDataBinding(layoutRes = R.layout.item_discover_multiview_recommend),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick,
                    handleVisitorMode = handleVisitorMode,
                    onSortButtonClick = onSortButtonClick
                )
                return recommendViewHolder
            }
        }
    }
}