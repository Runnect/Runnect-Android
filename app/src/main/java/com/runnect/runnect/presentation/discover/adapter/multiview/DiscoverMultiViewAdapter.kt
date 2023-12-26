package com.runnect.runnect.presentation.discover.adapter.multiview

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
    private val multiViewHolderFactory by lazy { DiscoverMultiViewHolderFactory() }
    private val currentList: List<MutableList<DiscoverMultiViewItem>> =
        multiViewItems.map { it.toMutableList() }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position].first()) {
            is MarathonCourse -> DiscoverMultiViewType.MARATHON.ordinal
            is RecommendHeader -> DiscoverMultiViewType.RECOMMEND_HEADER.ordinal
            is RecommendCourse -> DiscoverMultiViewType.RECOMMEND_COURSE.ordinal
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverMultiViewHolder {
        return multiViewHolderFactory.createMultiViewHolder(
            parent = parent,
            viewType = DiscoverMultiViewType.values()[viewType],
            onHeartButtonClick = onHeartButtonClick,
            onCourseItemClick = onCourseItemClick,
            handleVisitorMode = handleVisitorMode
        )
    }

    override fun onBindViewHolder(holder: DiscoverMultiViewHolder, position: Int) {
        when (holder) {
            is DiscoverMultiViewHolder.MarathonCourseViewHolder -> {
                (currentList[position] as? List<MarathonCourse>)?.let {
                    holder.bind(it)
                }
            }

            is DiscoverMultiViewHolder.RecommendHeaderViewHolder -> {
                (currentList[position] as? List<RecommendHeader>)?.let {
                    holder.bind(it)
                }
            }

            is DiscoverMultiViewHolder.RecommendCourseViewHolder -> {
                (currentList[position] as? List<RecommendCourse>)?.let {
                    holder.bind(it)
                }
            }
        }
    }

    fun addRecommendCourseNextPage(nextPageCourses: List<RecommendCourse>) {
        // 외부 리사이클러뷰의 추천 코스 리스트 갱신 -> 내부 리사이클러뷰 재바인딩 -> 새로운 데이터 submitList
        val recommendPosition = DiscoverMultiViewType.RECOMMEND_COURSE.ordinal
        currentList[recommendPosition].addAll(nextPageCourses)
        notifyItemChanged(recommendPosition)
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
                val position = DiscoverMultiViewType.MARATHON.ordinal
                val targetIndex = currentList[position].indexOf(targetItem)
                multiViewHolderFactory.marathonViewHolder.updateMarathonCourseItem(
                    targetIndex = targetIndex,
                    updatedCourse = updatedCourse
                )
            }

            is RecommendCourse -> {
                val position = DiscoverMultiViewType.RECOMMEND_COURSE.ordinal
                val targetIndex = currentList[position].indexOf(targetItem)
                multiViewHolderFactory.recommendCourseViewHolder.updateRecommendCourseItem(
                    targetIndex = targetIndex,
                    updatedCourse = updatedCourse
                )
            }

            else -> {}
        }
    }
}