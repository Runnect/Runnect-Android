package com.runnect.runnect.presentation.discover.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendHeader
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse

class DiscoverMultiViewAdapter(
    multiViewItems: List<List<DiscoverMultiViewItem>>,
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
    private val handleVisitorMode: () -> Unit,
) : RecyclerView.Adapter<DiscoverMultiViewHolder>() {
    private val currentList = multiViewItems.toMutableList()
    private lateinit var marathonViewHolder: DiscoverMultiViewHolder.MarathonCourseViewHolder
    private lateinit var recommendViewHolder: DiscoverMultiViewHolder.RecommendCourseViewHolder

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position].first()) {
            is MarathonCourse -> DiscoverMultiViewType.MARATHON.ordinal
            is RecommendHeader -> DiscoverMultiViewType.RECOMMEND_HEADER.ordinal
            is RecommendCourse -> DiscoverMultiViewType.RECOMMEND_COURSE.ordinal
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverMultiViewHolder {
        return DiscoverMultiViewHolderFactory().createMultiViewHolder(
            parent = parent,
            viewType = DiscoverMultiViewType.values()[viewType],
            onHeartButtonClick = onHeartButtonClick,
            onCourseItemClick = onCourseItemClick,
            handleVisitorMode = handleVisitorMode
        )
    }

    override fun onBindViewHolder(holder: DiscoverMultiViewHolder, position: Int) {
        val currentMultiViewItem = currentList[position]

        when (holder) {
            is DiscoverMultiViewHolder.MarathonCourseViewHolder -> {
                marathonViewHolder = holder
                (currentMultiViewItem as? List<MarathonCourse>)?.let {
                    holder.bind(it)
                }
            }

            is DiscoverMultiViewHolder.RecommendHeaderViewHolder -> {
                (currentMultiViewItem as? List<RecommendHeader>)?.let {
                    holder.bind(it)
                }
            }

            is DiscoverMultiViewHolder.RecommendCourseViewHolder -> {
                recommendViewHolder = holder
                (currentMultiViewItem as? List<RecommendCourse>)?.let {
                    holder.bind(it)
                }
            }
        }
    }

    fun addRecommendCourseNextPage(nextPageCourses: List<RecommendCourse>) {
        currentList.add(nextPageCourses)
        notifyItemInserted(itemCount - 1)
    }

    fun updateCourseItem(
        publicCourseId: Int,
        updatedCourse: EditableDiscoverCourse
    ) {
        val targetItem = currentList.flatten().find { item ->
            item.id == publicCourseId
        } ?: return

        when (targetItem) {
            is MarathonCourse -> {
                marathonViewHolder.updateMarathonCourseItem(
                    publicCourseId = publicCourseId,
                    updatedCourse = updatedCourse
                )
            }

            else -> {
                recommendViewHolder.updateRecommendCourseItem(
                    publicCourseId = publicCourseId,
                    updatedCourse = updatedCourse
                )
            }
        }
    }
}