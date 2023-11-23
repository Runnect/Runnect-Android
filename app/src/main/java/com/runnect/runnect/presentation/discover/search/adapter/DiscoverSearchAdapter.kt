package com.runnect.runnect.presentation.discover.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.databinding.ItemDiscoverCourseBinding
import com.runnect.runnect.util.callback.ItemDiffCallback
import com.runnect.runnect.util.callback.OnCourseItemClicked
import com.runnect.runnect.util.callback.OnScrapButtonClicked

class DiscoverSearchAdapter(
    context: Context,
    private val itemClick: OnCourseItemClicked,
    private val heartClick: OnScrapButtonClicked
) : ListAdapter<CourseSearchDTO, DiscoverSearchAdapter.SearchViewHolder>(diffUtil) {
    private val inflater by lazy { LayoutInflater.from(context) }

    class SearchViewHolder(
        private val binding: ItemDiscoverCourseBinding,
        private val itemClick: OnCourseItemClicked,
        private val heartClick: OnScrapButtonClicked
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: CourseSearchDTO) {
            binding.ivItemDiscoverCourseMap.load(data.image)
            binding.tvItemDiscoverCourseTitle.text = data.title
            binding.tvItemDiscoverCourseLocation.text = data.departure
            binding.ivItemDiscoverCourseScrap.isSelected = data.scrap

            binding.ivItemDiscoverCourseScrap.setOnClickListener {
                it.isSelected = !it.isSelected
                heartClick.scrapCourse(data.id, it.isSelected)
            }

            binding.ivItemDiscoverCourseMap.setOnClickListener {
                itemClick.selectItem(data.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ItemDiscoverCourseBinding.inflate(inflater, parent, false),
            itemClick, heartClick
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
