package com.runnect.runnect.presentation.discover.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.databinding.ItemDiscoverCourseBinding
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.callback.listener.OnHeartButtonClick
import com.runnect.runnect.util.callback.listener.OnRecommendItemClick

class DiscoverSearchAdapter(
    private val onRecommendItemClick: OnRecommendItemClick,
    private val onHeartButtonClick: OnHeartButtonClick
) : ListAdapter<CourseSearchDTO, DiscoverSearchAdapter.SearchViewHolder>(diffUtil) {
    class SearchViewHolder(
        private val binding: ItemDiscoverCourseBinding,
        private val onRecommendItemClick: OnRecommendItemClick,
        private val onHeartButtonClick: OnHeartButtonClick
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: CourseSearchDTO) {

            binding.ivItemDiscoverCourseMap.load(data.image)
            binding.tvItemDiscoverCourseTitle.text = data.title
            binding.tvItemDiscoverCourseLocation.text = data.departure
            binding.ivItemDiscoverCourseScrap.isSelected = data.scrap

            binding.ivItemDiscoverCourseScrap.setOnClickListener {
                it.isSelected = !it.isSelected
                onHeartButtonClick.scrapCourse(data.id, it.isSelected)
            }

            binding.ivItemDiscoverCourseMap.setOnClickListener {
                onRecommendItemClick.selectItem(data.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ItemDiscoverCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onRecommendItemClick, onHeartButtonClick
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    companion object {
        private val diffUtil = ItemDiffCallback<CourseSearchDTO>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
