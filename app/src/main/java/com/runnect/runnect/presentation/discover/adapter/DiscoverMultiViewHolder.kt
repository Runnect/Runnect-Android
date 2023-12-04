package com.runnect.runnect.presentation.discover.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverMultiviewMarathonBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendBinding
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import com.runnect.runnect.util.custom.deco.DiscoverMarathonItemDecoration
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
        private val onNextPageLoad: (Int) -> Unit,
    ) : DiscoverMultiViewHolder(binding) {
        fun bind(currentPageNumber: Int, recommendCourses: List<RecommendCourse>) {
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
                        horizontalSpaceSize = 6,
                        topSpaceSize = 20
                    )
                )
                initScrollListener(currentPageNumber, this)
            }
        }

        private fun initScrollListener(currentPageNumber: Int, recyclerView: RecyclerView) {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // TODO: 다음 페이지가 있다면!! 요청 보내기
                    //onNextPageLoad(currentPageNumber + 1)
                }
            })
        }

        companion object {
            private const val SCROLL_DIRECTION = 1
        }
    }
}
