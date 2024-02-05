package com.runnect.runnect.presentation.discover.adapter.multiview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import timber.log.Timber

class DiscoverMultiViewAdapter(
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
    private val handleVisitorMode: () -> Unit,
    private val onSortButtonClick: (String) -> Unit
) : RecyclerView.Adapter<DiscoverMultiViewHolder>() {
    private val multiViewHolderFactory by lazy { DiscoverMultiViewHolderFactory() }
    private val marathonCourses = arrayListOf<MarathonCourse>()
    private val recommendCourses = arrayListOf<RecommendCourse>()
    private var viewTypes = arrayListOf<Int>()

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
                holder.bind(marathonCourses)
            }

            is DiscoverMultiViewHolder.RecommendCourseViewHolder -> {
                holder.bind(recommendCourses)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewTypes.getOrNull(position) ?: DiscoverMultiViewType.MARATHON.ordinal
    }

    override fun getItemCount(): Int = viewTypes.size

    fun initMarathonCourses(items: List<MarathonCourse>) {
        marathonCourses.apply {
            clear()
            addAll(items)
        }
        refreshViewHolder(viewType = DiscoverMultiViewType.MARATHON.ordinal)
    }

    fun initRecommendCourses(items: List<RecommendCourse>) {
        recommendCourses.apply {
            clear()
            addAll(items)
        }
        refreshViewHolder(viewType = DiscoverMultiViewType.RECOMMEND.ordinal)
    }

    private fun refreshViewHolder(viewType: Int) {
        // 데이터 로딩 시간과 관계 없이
        // 항상 마라톤 다음에 추천 코스가 추가되도록 뷰 타입 리스트 갱신
        viewTypes = initItemViewTypes()

        val startPosition = getViewTypeStartPosition(viewType)
        if (startPosition >= 0) {
            notifyItemChanged(startPosition)
        }
    }

    private fun initItemViewTypes(): ArrayList<Int> {
        return arrayListOf<Int>().apply {
            if (marathonCourses.isNotEmpty()) {
                add(DiscoverMultiViewType.MARATHON.ordinal)
            }

            if (recommendCourses.isNotEmpty()) {
                add(DiscoverMultiViewType.RECOMMEND.ordinal)
            }
        }
    }

    private fun getViewTypeStartPosition(viewType: Int): Int {
        return viewTypes.indexOfFirst { viewType == it }
    }

    fun addRecommendCourseNextPage(nextPageItems: List<RecommendCourse>) {
        recommendCourses.addAll(nextPageItems)
        Timber.d("item count in outer recyclerview: ${nextPageItems.size} ${recommendCourses.size}")

        multiViewHolderFactory.recommendCourseAdapter.addRecommendCourseNextPage(nextPageItems)
    }

    fun updateRecommendCourseBySorting(firstPageItems: List<RecommendCourse>) {
        recommendCourses.apply {
            clear()
            addAll(firstPageItems)
        }
        Timber.d("item count in outer recyclerview: ${firstPageItems.size} ${recommendCourses.size}")

        multiViewHolderFactory.recommendCourseAdapter.updateRecommendCourseBySorting(firstPageItems)
    }

    fun updateCourseItem(
        publicCourseId: Int,
        updatedCourse: EditableDiscoverCourse
    ) {
        val multiViewItems = marathonCourses + recommendCourses
        val targetItem = multiViewItems.find { item ->
            item.id == publicCourseId
        } ?: return

        when (targetItem) {
            is MarathonCourse -> {
                val targetIndex = marathonCourses.indexOf(targetItem)
                multiViewHolderFactory.marathonCourseAdapter.updateMarathonCourseItem(
                    targetIndex = targetIndex,
                    updatedCourse = updatedCourse
                )
            }

            is RecommendCourse -> {
                val targetIndex = recommendCourses.indexOf(targetItem)
                multiViewHolderFactory.recommendCourseAdapter.updateRecommendCourseItem(
                    targetIndex = targetIndex,
                    updatedCourse = updatedCourse
                )
            }
        }
    }
}