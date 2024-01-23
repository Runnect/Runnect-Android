package com.runnect.runnect.presentation.discover.adapter.multiview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse

class DiscoverMultiViewAdapter(
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
    private val handleVisitorMode: () -> Unit,
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
            handleVisitorMode = handleVisitorMode
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
        // 데이터 로딩 시간 차이와 관계 없이
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

    fun addRecommendCourseNextPage(items: List<RecommendCourse>) {
        multiViewHolderFactory.recommendCourseAdapter.addRecommendCourseNextPage(items)
    }

    fun updateCourseItem(
        publicCourseId: Int,
        updatedCourse: EditableDiscoverCourse
    ) {
//        val targetItem = currentList.flatten().find { item ->
//            item.id == publicCourseId
//        } ?: return
//
//        when (targetItem) {
//            is MarathonCourse -> {
//                val position = DiscoverMultiViewType.MARATHON.ordinal
//                val targetIndex = currentList[position].indexOf(targetItem)
//                multiViewHolderFactory.marathonViewHolder.updateMarathonCourseItem(
//                    targetIndex = targetIndex,
//                    updatedCourse = updatedCourse
//                )
//            }
//
//            is RecommendCourse -> {
//                val position = DiscoverMultiViewType.RECOMMEND.ordinal
//                val targetIndex = currentList[position].indexOf(targetItem)
//                multiViewHolderFactory.recommendViewHolder.updateRecommendCourseItem(
//                    targetIndex = targetIndex,
//                    updatedCourse = updatedCourse
//                )
//            }
//        }
    }
}