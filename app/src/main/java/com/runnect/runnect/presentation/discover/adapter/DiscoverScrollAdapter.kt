package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.runnect.runnect.databinding.LayoutDiscoverBannerBinding
import com.runnect.runnect.databinding.LayoutDiscoverMarathonBinding
import com.runnect.runnect.databinding.LayoutDiscoverRecommendBinding
import com.runnect.runnect.domain.entity.DiscoverScrollItem
import com.runnect.runnect.domain.entity.DiscoverScrollItem.*
import com.runnect.runnect.util.callback.diff.ItemDiffCallback

class DiscoverScrollAdapter(
    private val bannerAdapter: BannerAdapter,
    private val marathonCourseAdapter: DiscoverCourseAdapter,
    private val recommendCourseAdapter: DiscoverCourseAdapter
) : ListAdapter<DiscoverScrollItem, DiscoverScrollViewHolder>(diffUtil) {
    enum class DiscoverViewType {
        BANNER,
        MARATHON,
        RECOMMEND
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is Banner -> DiscoverViewType.BANNER.ordinal
            is MarathonCourse -> DiscoverViewType.MARATHON.ordinal
            is RecommendCourse -> DiscoverViewType.RECOMMEND.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverScrollViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            DiscoverViewType.BANNER.ordinal -> {
                DiscoverScrollViewHolder.BannerViewHolder(
                    binding = LayoutDiscoverBannerBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    adapter = bannerAdapter
                )
            }

            DiscoverViewType.MARATHON.ordinal -> {
                DiscoverScrollViewHolder.MarathonCourseViewHolder(
                    binding = LayoutDiscoverMarathonBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    marathonCourseAdapter = marathonCourseAdapter
                )
            }

            DiscoverViewType.RECOMMEND.ordinal -> {
                DiscoverScrollViewHolder.RecommendCourseViewHolder(
                    binding = LayoutDiscoverRecommendBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    recommendCourseAdapter = recommendCourseAdapter
                )
            }

            else -> { throw IllegalArgumentException("Unknown Item View Type") }
        }
    }

    override fun onBindViewHolder(holder: DiscoverScrollViewHolder, position: Int) {
        when (holder) {
            is DiscoverScrollViewHolder.BannerViewHolder -> {
                holder.bind()
            }

            is DiscoverScrollViewHolder.MarathonCourseViewHolder -> {
                holder.bind()
            }

            is DiscoverScrollViewHolder.RecommendCourseViewHolder -> {
                holder.bind()
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverScrollItem>(
            onItemsTheSame = { old, new -> old === new },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}