package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
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
    private lateinit var marathonViewHolder: DiscoverMultiViewHolder.MarathonCourseViewHolder
    private lateinit var recommendViewHolder: DiscoverMultiViewHolder.RecommendCourseViewHolder

    override fun getItemViewType(position: Int): Int {
        return when (multiViewItems[position].first()) {
            is MarathonCourse -> DiscoverCourseType.MARATHON.ordinal
            is RecommendCourse -> DiscoverCourseType.RECOMMEND.ordinal
        }
    }

    override fun getItemCount(): Int = multiViewItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverMultiViewHolder {
        return when (viewType) {
            DiscoverCourseType.MARATHON.ordinal -> {
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
                    handleVisitorMode = handleVisitorMode,
                )
            }
        }
    }
    override fun onBindViewHolder(holder: DiscoverMultiViewHolder, position: Int) {
        when (holder) {
            is DiscoverMultiViewHolder.MarathonCourseViewHolder -> {
                marathonViewHolder = holder
                (multiViewItems[position] as? List<MarathonCourse>)?.let {
                    holder.bind(it)
                }
            }

            is DiscoverMultiViewHolder.RecommendCourseViewHolder -> {
                recommendViewHolder = holder
                (multiViewItems[position] as? List<RecommendCourse>)?.let {
                    holder.bind(it)
                }
            }
        }
    }

    // todo: 추천 코스 목록 갱신하기 (다음 페이지 로딩)
    fun updateRecommendCourses(courses: List<RecommendCourse>) {
        recommendViewHolder.updateRecommendCourses(courses)
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