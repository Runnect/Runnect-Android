package com.runnect.runnect.presentation.discover.adapter.multiview

import android.view.ViewGroup
import com.runnect.runnect.R
import com.runnect.runnect.util.extension.getViewDataBinding

class DiscoverMultiViewHolderFactory {
    lateinit var marathonViewHolder: DiscoverMultiViewHolder.MarathonCourseViewHolder
    lateinit var recommendCourseViewHolder: DiscoverMultiViewHolder.RecommendCourseViewHolder

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
                marathonViewHolder = DiscoverMultiViewHolder.MarathonCourseViewHolder(
                    binding = parent.getViewDataBinding(layoutRes = R.layout.item_discover_multiview_marathon),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick,
                    handleVisitorMode = handleVisitorMode
                )
                return marathonViewHolder
            }

            DiscoverMultiViewType.RECOMMEND_HEADER.ordinal -> {
                return DiscoverMultiViewHolder.RecommendHeaderViewHolder(
                    binding = parent.getViewDataBinding(layoutRes = R.layout.item_discover_multiview_recommend_header),
                    onSortButtonClick = onSortButtonClick
                )
            }

            else -> {
                recommendCourseViewHolder = DiscoverMultiViewHolder.RecommendCourseViewHolder(
                    binding = parent.getViewDataBinding(layoutRes = R.layout.item_discover_multiview_recommend_course),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick,
                    handleVisitorMode = handleVisitorMode
                )
                return recommendCourseViewHolder
            }
        }
    }
}