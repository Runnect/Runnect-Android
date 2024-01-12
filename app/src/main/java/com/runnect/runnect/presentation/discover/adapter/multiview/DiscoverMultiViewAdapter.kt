package com.runnect.runnect.presentation.discover.adapter.multiview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse

class DiscoverMultiViewAdapter(
    multiViewItems: List<List<DiscoverMultiViewItem>>,
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
    private val handleVisitorMode: () -> Unit,
) : RecyclerView.Adapter<DiscoverMultiViewHolder>() {
    private val multiViewHolderFactory by lazy { DiscoverMultiViewHolderFactory() }
    private val currentList: MutableList<MutableList<DiscoverMultiViewItem>> =
        multiViewItems.map { it.toMutableList() }.toMutableList()

    override fun getItemViewType(position: Int): Int {
        if (currentList.isEmpty())
            return DiscoverMultiViewType.MARATHON.ordinal

        val multiViewItem = currentList[position].first()
        return multiViewItem.getMultiViewType().ordinal
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
        if (currentList.isEmpty()) return

        when (holder) {
            is DiscoverMultiViewHolder.MarathonCourseViewHolder -> {
                (currentList[position] as? List<MarathonCourse>)?.let {
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

    fun addMultiViewItem(courses: List<DiscoverMultiViewItem>) {
        currentList.add(courses.toMutableList())
        notifyItemInserted(itemCount - 1)
    }

    fun addRecommendCourseNextPage(nextPageCourses: List<RecommendCourse>) {
        // 외부 리사이클러뷰의 추천 코스 리스트 갱신 -> 내부 리사이클러뷰 재바인딩 -> 새로운 데이터 submitList
        val position = DiscoverMultiViewType.RECOMMEND.ordinal
        currentList[position].addAll(nextPageCourses)
        notifyItemChanged(position)
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
                val position = DiscoverMultiViewType.RECOMMEND.ordinal
                val targetIndex = currentList[position].indexOf(targetItem)
                multiViewHolderFactory.recommendViewHolder.updateRecommendCourseItem(
                    targetIndex = targetIndex,
                    updatedCourse = updatedCourse
                )
            }
        }
    }
}