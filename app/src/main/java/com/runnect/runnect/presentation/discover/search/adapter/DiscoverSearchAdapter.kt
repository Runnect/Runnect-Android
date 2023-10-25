package com.runnect.runnect.presentation.discover.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.databinding.ItemDiscoverCourseInfoBinding
import com.runnect.runnect.util.callback.OnHeartClick
import com.runnect.runnect.util.callback.OnItemClick
import com.runnect.runnect.util.callback.ItemDiffCallback

class DiscoverSearchAdapter(
    context: Context,
    private val itemClick: OnItemClick,
    private val heartClick: OnHeartClick
) : ListAdapter<CourseSearchDTO, DiscoverSearchAdapter.SearchViewHolder>(diffUtil) {
    private val inflater by lazy { LayoutInflater.from(context) }

    class SearchViewHolder(
        private val binding: ItemDiscoverCourseInfoBinding,
        private val itemClick: OnItemClick,
        private val heartClick: OnHeartClick
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: CourseSearchDTO) {
            binding.ivItemDiscoverCourseInfoMap.load(data.image)
            binding.tvItemDiscoverCourseInfoTitle.text = data.title
            binding.tvItemDiscoverCourseInfoLocation.text = data.departure
            binding.ivItemDiscoverCourseInfoScrap.isSelected = data.scrap
            binding.ivItemDiscoverCourseInfoScrap.setOnClickListener {
                it.isSelected = !it.isSelected
                heartClick.scrapCourse(data.id, it.isSelected)
            }
            binding.ivItemDiscoverCourseInfoMap.setOnClickListener {
                itemClick.selectItem(data.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ItemDiscoverCourseInfoBinding.inflate(inflater, parent, false),
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
