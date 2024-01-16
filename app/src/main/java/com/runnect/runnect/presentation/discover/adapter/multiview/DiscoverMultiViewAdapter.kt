package com.runnect.runnect.presentation.discover.adapter.multiview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.*
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import timber.log.Timber

class DiscoverMultiViewAdapter(
    multiViewItems: List<List<DiscoverMultiViewItem>>,
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
    private val handleVisitorMode: () -> Unit,
    private val onSortButtonClick: (String) -> Unit
) : RecyclerView.Adapter<DiscoverMultiViewHolder>() {
    private val multiViewHolderFactory by lazy { DiscoverMultiViewHolderFactory() }
    private val currentList: MutableList<MutableList<DiscoverMultiViewItem>> =
        multiViewItems.map { it.toMutableList() }.toMutableList()

    override fun getItemViewType(position: Int): Int {
        val multiViewItem = currentList[position].first()
        return multiViewItem.getMultiViewType().ordinal
    }

    override fun getItemCount(): Int = currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverMultiViewHolder {
        return multiViewHolderFactory.createMultiViewHolder(
            parent = parent,
            viewType = viewType,
            onHeartButtonClick = onHeartButtonClick,
            onCourseItemClick = onCourseItemClick,
            handleVisitorMode = handleVisitorMode,
            onSortButtonClick = onSortButtonClick
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
                holder.bind()
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

    fun updateRecommendCourseBySorting(courses: List<RecommendCourse>) {
        val position = DiscoverMultiViewType.RECOMMEND_COURSE.ordinal
        currentList[position] = courses.toMutableList()
        notifyItemChanged(position)
    }

    fun addRecommendCourseNextPage(nextPageCourses: List<RecommendCourse>) {
        // 외부 리사이클러뷰의 추천 코스 리스트 갱신 -> 내부 리사이클러뷰 재바인딩 -> 새로운 데이터 submitList
        val position = DiscoverMultiViewType.RECOMMEND_COURSE.ordinal
        currentList[position].addAll(nextPageCourses)
        Timber.d("페이지가 추가된 배열 크기: ${currentList[position].size}")
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