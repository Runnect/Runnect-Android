package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverMultiviewMarathonBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendBinding
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import timber.log.Timber

class DiscoverMultiViewAdapter(
    private val multiViewItems: List<List<DiscoverMultiViewItem>>,
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
    private val currentPageNumber: Int,
    private val onNextPageLoad: (Int) -> Unit,
) : RecyclerView.Adapter<DiscoverMultiViewHolder>() {

    enum class MultiViewType {
        MARATHON,
        RECOMMEND
    }

    override fun getItemViewType(position: Int): Int {
        return when (multiViewItems[position].first()) {
            is MarathonCourse -> MultiViewType.MARATHON.ordinal
            is RecommendCourse -> MultiViewType.RECOMMEND.ordinal
        }
    }

    override fun getItemCount(): Int = multiViewItems.size

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

            else -> {
                DiscoverMultiViewHolder.RecommendCourseViewHolder(
                    binding = ItemDiscoverMultiviewRecommendBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onHeartButtonClick = onHeartButtonClick,
                    onCourseItemClick = onCourseItemClick,
                    onNextPageLoad = onNextPageLoad
                )
            }
        }
    }

    override fun onBindViewHolder(holder: DiscoverMultiViewHolder, position: Int) {
        when (holder) {
            is DiscoverMultiViewHolder.MarathonCourseViewHolder -> {
                (multiViewItems[position] as? List<MarathonCourse>)?.let {
                    holder.bind(it)
                }
            }

            is DiscoverMultiViewHolder.RecommendCourseViewHolder -> {
                (multiViewItems[position] as? List<RecommendCourse>)?.let {
                    holder.bind(currentPageNumber, it)
                }
            }
        }
    }
}