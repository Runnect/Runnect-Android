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
            is MarathonCourse -> DiscoverCourseType.MARATHON.ordinal
            is RecommendCourse -> DiscoverCourseType.RECOMMEND.ordinal
        }
    }

    override fun getItemCount(): Int = currentList.size

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
                (currentList[position] as? List<MarathonCourse>)?.let {
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

    // todo: 추천 코스 목록 갱신하기 (다음 페이지 로딩)
    fun updateRecommendCourses(nextPageCourses: List<RecommendCourse>) {
        val position = DiscoverCourseType.RECOMMEND.ordinal
        val newCourses = currentList[position].plus(nextPageCourses)
        currentList[position] = newCourses

        // todo: 이걸 호출하면 바로 이전 페이지의 첫번째 아이템 위치로 스크롤이 초기화 된다......
        notifyItemChanged(position)
    }

    // todo: 다음 페이지 요청에 따라 currentList 데이터도 달라져야 한다.
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