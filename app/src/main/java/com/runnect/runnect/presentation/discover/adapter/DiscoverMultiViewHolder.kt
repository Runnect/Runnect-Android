package com.runnect.runnect.presentation.discover.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverMultiviewMarathonBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendBinding
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration

sealed class DiscoverMultiViewHolder(binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    class MarathonCourseViewHolder(
        private val binding: ItemDiscoverMultiviewMarathonBinding,
        private val onHeartButtonClick: (Int, Boolean) -> Unit,
        private val onCourseItemClick: (Int) -> Unit
    ) : DiscoverMultiViewHolder(binding) {
        fun bind(marathonCourses: List<MarathonCourse>) {
            binding.rvDiscoverMarathon.apply {
                setHasFixedSize(true)
                adapter = DiscoverMarathonAdapter(onHeartButtonClick, onCourseItemClick).apply {
                    submitList(marathonCourses)
                }
                // todo: 아이템 간의 여백 조정
            }
        }
    }

    class RecommendCourseViewHolder(
        private val binding: ItemDiscoverMultiviewRecommendBinding,
        private val onHeartButtonClick: (Int, Boolean) -> Unit,
        private val onCourseItemClick: (Int) -> Unit
    ) : DiscoverMultiViewHolder(binding) {
        fun bind(recommendCourses: List<RecommendCourse>) {
            binding.rvDiscoverRecommend.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 2)
                adapter = DiscoverRecommendAdapter(onHeartButtonClick, onCourseItemClick).apply {
                    submitList(recommendCourses)
                }
                addItemDecoration(
                    GridSpacingItemDecoration(
                        context = context,
                        spanCount = 2,
                        horizontalSpacing = 6,
                        topSpacing = 20
                    )
                )
            }
        }
    }
}
