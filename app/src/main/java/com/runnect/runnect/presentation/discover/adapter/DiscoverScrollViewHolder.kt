package com.runnect.runnect.presentation.discover.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.LayoutDiscoverBannerBinding
import com.runnect.runnect.databinding.LayoutDiscoverMarathonBinding
import com.runnect.runnect.databinding.LayoutDiscoverRecommendBinding
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration

sealed class DiscoverScrollViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    class BannerViewHolder(
        private val binding: LayoutDiscoverBannerBinding,
        private val adapter: BannerAdapter
    ) : DiscoverScrollViewHolder(binding) {
        fun bind() {
            binding.vpDiscoverBanner.adapter = adapter
        }
    }

    class MarathonCourseViewHolder(
        private val binding: LayoutDiscoverMarathonBinding,
        private val marathonCourseAdapter: DiscoverCourseAdapter
    ) : DiscoverScrollViewHolder(binding) {
        fun bind() {
            initMarathonCourseRecyclerView()
        }

        private fun initMarathonCourseRecyclerView() {
            binding.rvDiscoverMarathon.apply {
                setHasFixedSize(true)
                adapter = marathonCourseAdapter
                // todo: 아이템 간의 여백 조정
            }
        }
    }

    class RecommendCourseViewHolder(
        private val binding: LayoutDiscoverRecommendBinding,
        private val recommendCourseAdapter: DiscoverCourseAdapter
    ) : DiscoverScrollViewHolder(binding) {
        fun bind() {
            initRecommendCourseRecyclerView()
        }

        private fun initRecommendCourseRecyclerView() {
            binding.rvDiscoverRecommend.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(binding.root.context, 2)
                adapter = recommendCourseAdapter
                addItemDecoration(
                    GridSpacingItemDecoration(
                        context = binding.root.context,
                        spanCount = 2,
                        horizontalSpacing = 6,
                        topSpacing = 20
                    )
                )
            }
        }
    }
}
