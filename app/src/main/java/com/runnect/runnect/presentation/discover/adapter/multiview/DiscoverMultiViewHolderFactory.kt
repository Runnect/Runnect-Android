package com.runnect.runnect.presentation.discover.adapter.multiview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.runnect.runnect.R

class DiscoverMultiViewHolderFactory {
    lateinit var marathonViewHolder: DiscoverMultiViewHolder.MarathonCourseViewHolder
    lateinit var recommendViewHolder: DiscoverMultiViewHolder.RecommendCourseViewHolder

    fun createMultiViewHolder(
        parent: ViewGroup,
        viewType: DiscoverMultiViewType,
        onHeartButtonClick: (Int, Boolean) -> Unit,
        onCourseItemClick: (Int) -> Unit,
        handleVisitorMode: () -> Unit,
    ): DiscoverMultiViewHolder {
        when (viewType) {
            DiscoverMultiViewType.MARATHON -> {
                marathonViewHolder = DiscoverMultiViewHolder.MarathonCourseViewHolder(
                    binding = getViewBinding(parent, R.layout.item_discover_multiview_marathon),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick,
                    handleVisitorMode = handleVisitorMode
                )
                return marathonViewHolder
            }

            DiscoverMultiViewType.RECOMMEND -> {
                recommendViewHolder = DiscoverMultiViewHolder.RecommendCourseViewHolder(
                    binding = getViewBinding(
                        parent,
                        R.layout.item_discover_multiview_recommend
                    ),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick,
                    handleVisitorMode = handleVisitorMode
                )
                return recommendViewHolder
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