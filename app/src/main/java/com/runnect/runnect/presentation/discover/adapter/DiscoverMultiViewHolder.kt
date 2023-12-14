package com.runnect.runnect.presentation.discover.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverMultiviewMarathonBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendBinding
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import com.runnect.runnect.util.custom.deco.DiscoverMarathonItemDecoration
import com.runnect.runnect.util.custom.deco.DiscoverRecommendItemDecoration

sealed class DiscoverMultiViewHolder(binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    class MarathonCourseViewHolder(
        private val binding: ItemDiscoverMultiviewMarathonBinding,
        private val onHeartButtonClick: (Int, Boolean) -> Unit,
        private val onCourseItemClick: (Int) -> Unit,
        private val handleVisitorMode: () -> Unit
    ) : DiscoverMultiViewHolder(binding) {
        fun bind(marathonCourses: List<MarathonCourse>) {
            binding.rvDiscoverMarathon.apply {
                setHasFixedSize(true)

                adapter = DiscoverMarathonAdapter(
                    onHeartButtonClick,
                    onCourseItemClick,
                    handleVisitorMode
                ).apply {
                    submitList(marathonCourses)
                }

                addItemDecoration(
                    DiscoverMarathonItemDecoration(
                        context = context,
                        spaceSize = 10,
                        itemCount = marathonCourses.size
                    )
                )
            }
        }
    }

    class RecommendCourseViewHolder(
        private val binding: ItemDiscoverMultiviewRecommendBinding,
        private val onHeartButtonClick: (Int, Boolean) -> Unit,
        private val onCourseItemClick: (Int) -> Unit,
        private val handleVisitorMode: () -> Unit,
    ) : DiscoverMultiViewHolder(binding) {
        fun bind(recommendCourses: List<RecommendCourse>) {
            binding.rvDiscoverRecommend.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 2)

                adapter = DiscoverRecommendAdapter(
                    onHeartButtonClick,
                    onCourseItemClick,
                    handleVisitorMode
                ).apply {
                    submitList(recommendCourses)
                }

                addItemDecoration(
                    DiscoverRecommendItemDecoration(
                        context = context,
                        rightSpacing = 6,
                        bottomSpacing = 20,
                        spanCount = 2
                    )
                )
            }
        }

        private fun initScrollListener(currentPageNumber: Int, recyclerView: RecyclerView) {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // TODO: 스크롤이 최하단까지 내려간 경우, 다음 페이지 요청하기 (다음 페이지가 있는 경우에만)
                }
            })
        }
    }
}
