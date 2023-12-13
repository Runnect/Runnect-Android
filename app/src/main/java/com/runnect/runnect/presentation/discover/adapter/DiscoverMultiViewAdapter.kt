package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverMultiviewMarathonBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendBinding
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import timber.log.Timber

class DiscoverMultiViewAdapter(
    private val multiViewItems: List<List<DiscoverMultiViewItem>>,
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
    private val handleVisitorMode: () -> Unit,
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
                    onCourseItemClick = onCourseItemClick,
                    handleVisitorMode = handleVisitorMode
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
                    handleVisitorMode = handleVisitorMode
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
                    holder.bind(it)
                }
            }
        }
    }

    fun updateCourseItem(
        publicCourseId: Int,
        updatedCourse: EditableDiscoverCourse
    ) {
        for (courses in multiViewItems) {
            for (course in courses) {
                // todo: 해당하는 코스 id 발견하여 아이템 갱신하고 나면, 바로 함수 종료시키기
                if (findCourseItemByViewType(publicCourseId, course, updatedCourse)) {
                    Timber.e("SUCCESS COURSE ITEM UPDATE: ${publicCourseId}")
                    return
                }
            }
        }
    }

    private fun findCourseItemByViewType(
        publicCourseId: Int,
        course: DiscoverMultiViewItem,
        updatedCourse: EditableDiscoverCourse
    ): Boolean {
        when (course) {
            is MarathonCourse -> {
                if (course.id == publicCourseId) {
                    updateMarathonCourseItem(course, updatedCourse)
                    return true
                }
            }

            is RecommendCourse -> {
                if (course.id == publicCourseId) {
                    updateRecommendCourseItem(course, updatedCourse)
                    return true
                }
            }
        }

        return false
    }

    private fun updateMarathonCourseItem(
        course: MarathonCourse,
        updatedCourse: EditableDiscoverCourse
    ) {
        course.title = updatedCourse.title
        course.scrap = updatedCourse.scrap

        val marathonCourses = multiViewItems[0]
        val position = marathonCourses.indexOf(course)
        notifyItemChanged(position)
        Timber.e("UPDATE ITEM POSITION : ${position}")
    }

    private fun updateRecommendCourseItem(
        course: RecommendCourse,
        updatedCourse: EditableDiscoverCourse
    ) {
        course.title = updatedCourse.title
        course.scrap = updatedCourse.scrap
//        notifyDataSetChanged()
    }
}