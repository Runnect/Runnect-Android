package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverMultiviewMarathonBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendCourseBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendHeaderBinding
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.*
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
        return when (viewType) {
            DiscoverMultiViewType.MARATHON.ordinal -> {
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

            DiscoverMultiViewType.RECOMMEND_HEADER.ordinal -> {
                DiscoverMultiViewHolder.RecommendHeaderViewHolder(
                    binding = ItemDiscoverMultiviewRecommendHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                DiscoverMultiViewHolder.RecommendCourseViewHolder(
                    binding = ItemDiscoverMultiviewRecommendCourseBinding.inflate(
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
                recommendViewHolder = holder
                (currentList[position] as? List<RecommendCourse>)?.let {
                    holder.bind(it)
                }
            }
        }
    }

    // todo: 추천 코스 헤더 텍스트를 별도의 멀티 뷰 타입으로 분리하자! 팩토리 패턴 적용!
    fun loadRecommendCourseNextPage(nextPageCourses: List<RecommendCourse>) {
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