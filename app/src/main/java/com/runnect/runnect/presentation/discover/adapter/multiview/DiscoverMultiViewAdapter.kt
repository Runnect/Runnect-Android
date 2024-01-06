package com.runnect.runnect.presentation.discover.adapter.multiview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse

class DiscoverMultiViewAdapter(
    private val multiViewItems: MutableList<MutableList<DiscoverMultiViewItem>>,
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
    private val handleVisitorMode: () -> Unit,
    private val onSortButtonClick: (String) -> Unit
) : RecyclerView.Adapter<DiscoverMultiViewHolder>() {
    private val multiViewHolderFactory by lazy { DiscoverMultiViewHolderFactory() }

    override fun getItemViewType(position: Int): Int {
        return when (multiViewItems[position].first()) {
            is MarathonCourse -> DiscoverMultiViewType.MARATHON.ordinal
            is RecommendCourse -> DiscoverMultiViewType.RECOMMEND.ordinal
        }
    }

    override fun getItemCount(): Int = multiViewItems.size

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
                (multiViewItems[position] as? List<MarathonCourse>)?.let {
                    holder.bind(it)
                }
            }

            is DiscoverMultiViewHolder.RecommendCourseViewHolder -> {
                (multiViewItems[position] as? List<RecommendCourse>)?.let {
                    holder.bind(it)
                }
            }
        }
    }

    fun addMultiViewItem(courses: List<DiscoverMultiViewItem>) {
        multiViewItems.add(courses.toMutableList())
        notifyItemInserted(itemCount - 1)
    }

    fun updateRecommendCourseBySorting(courses: List<RecommendCourse>) {
        val position = DiscoverMultiViewType.RECOMMEND.ordinal
        multiViewItems[position] = courses.toMutableList()
        notifyItemChanged(position)
    }

    fun addRecommendCourseNextPage(nextPageCourses: List<RecommendCourse>) {
        // 외부 리사이클러뷰의 추천 코스 리스트 갱신 -> 내부 리사이클러뷰 재바인딩 -> 새로운 데이터 submitList
        val position = DiscoverMultiViewType.RECOMMEND.ordinal
        multiViewItems[position].addAll(nextPageCourses)
        notifyItemChanged(position)
    }

    fun updateCourseItem(
        publicCourseId: Int,
        updatedCourse: EditableDiscoverCourse
    ) {
        val targetItem = multiViewItems.flatten().find { item ->
            item.id == publicCourseId
        } ?: return

        when (targetItem) {
            is MarathonCourse -> {
                val position = DiscoverMultiViewType.MARATHON.ordinal
                val targetIndex = multiViewItems[position].indexOf(targetItem)
                multiViewHolderFactory.marathonViewHolder.updateMarathonCourseItem(
                    targetIndex = targetIndex,
                    updatedCourse = updatedCourse
                )
            }

            is RecommendCourse -> {
                val position = DiscoverMultiViewType.RECOMMEND.ordinal
                val targetIndex = multiViewItems[position].indexOf(targetItem)
                multiViewHolderFactory.recommendViewHolder.updateRecommendCourseItem(
                    targetIndex = targetIndex,
                    updatedCourse = updatedCourse
                )
            }
        }
    }
}