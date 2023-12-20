package com.runnect.runnect.presentation.discover.adapter.multiview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.runnect.runnect.R

class DiscoverMultiViewHolderFactory {
    fun createMultiViewHolder(
        parent: ViewGroup,
        viewType: DiscoverMultiViewType,
        onHeartButtonClick: (Int, Boolean) -> Unit,
        onCourseItemClick: (Int) -> Unit,
        handleVisitorMode: () -> Unit,
    ): DiscoverMultiViewHolder {
        return when (viewType) {
            DiscoverMultiViewType.MARATHON -> {
                DiscoverMultiViewHolder.MarathonCourseViewHolder(
                    binding = getViewBinding(parent, R.layout.item_discover_multiview_marathon),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick,
                    handleVisitorMode = handleVisitorMode
                )
            }

            DiscoverMultiViewType.RECOMMEND_HEADER -> {
                DiscoverMultiViewHolder.RecommendHeaderViewHolder(
                    binding = getViewBinding(
                        parent,
                        R.layout.item_discover_multiview_recommend_header
                    )
                )
            }

            DiscoverMultiViewType.RECOMMEND_COURSE -> {
                DiscoverMultiViewHolder.RecommendCourseViewHolder(
                    binding = getViewBinding(
                        parent,
                        R.layout.item_discover_multiview_recommend_course
                    ),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick,
                    handleVisitorMode = handleVisitorMode
                )
            }
        }
    }

    private fun <T : ViewDataBinding> getViewBinding(
        parent: ViewGroup,
        @LayoutRes layoutRes: Int
    ): T = DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        layoutRes,
        parent,
        false
    )
}