package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.runnect.runnect.databinding.ItemDiscoverMultiviewMarathonBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendBinding
import com.runnect.runnect.domain.entity.DiscoverMultiItem
import com.runnect.runnect.domain.entity.DiscoverMultiItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiItem.RecommendCourse
import com.runnect.runnect.util.callback.diff.ItemDiffCallback

class DiscoverMultiAdapter(
    private val marathonCourses: List<MarathonCourse>,
    private val recommendCourses: List<RecommendCourse>,
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
) : ListAdapter<DiscoverMultiItem, DiscoverMultiViewHolder>(diffUtil) {
    enum class MultiViewType {
        MARATHON,
        RECOMMEND
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is MarathonCourse -> MultiViewType.MARATHON.ordinal
            is RecommendCourse -> MultiViewType.RECOMMEND.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverMultiViewHolder {
        return when (viewType) {
            MultiViewType.MARATHON.ordinal -> {
                DiscoverMultiViewHolder.MarathonCourseViewHolder(
                    binding = ItemDiscoverMultiviewMarathonBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick
                )
            }

            MultiViewType.RECOMMEND.ordinal -> {
                DiscoverMultiViewHolder.RecommendCourseViewHolder(
                    binding = ItemDiscoverMultiviewRecommendBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick
                )
            }

            else -> { throw IllegalArgumentException("Unknown Item View Type") }
        }
    }

    override fun onBindViewHolder(holder: DiscoverMultiViewHolder, position: Int) {
        when (holder) {
            is DiscoverMultiViewHolder.MarathonCourseViewHolder -> {
                holder.bind(marathonCourses)
            }

            is DiscoverMultiViewHolder.RecommendCourseViewHolder -> {
                holder.bind(recommendCourses)
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverMultiItem>(
            onItemsTheSame = { old, new -> old === new },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}