package com.runnect.runnect.presentation.discover.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.runnect.runnect.data.dto.CourseInfoDTO
import com.runnect.runnect.databinding.ItemDiscoverCourseInfoBinding
import com.runnect.runnect.util.CourseInfoDiffUtilItemCallback
import com.runnect.runnect.util.callback.OnItemClick

class DiscoverSearchAdapter(context: Context, listener: OnItemClick) :
    ListAdapter<CourseInfoDTO, SearchViewHolder>(
        CourseInfoDiffUtilItemCallback()
    ) {
    private val inflater by lazy { LayoutInflater.from(context) }
    private val mCallback = listener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ItemDiscoverCourseInfoBinding.inflate(inflater, parent, false),
            mCallback
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }
}

class SearchViewHolder(
    private val binding: ItemDiscoverCourseInfoBinding,
    private val mCallback: OnItemClick
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(data: CourseInfoDTO) {
        binding.ivItemDiscoverCourseInfoMap.load(data.img)
        binding.tvItemDiscoverCourseInfoTitle.text = data.title
        binding.tvItemDiscoverCourseInfoLocation.text = data.location
        binding.ivItemDiscoverCourseInfoScrap.isSelected = data.isScraped
        binding.ivItemDiscoverCourseInfoScrap.setOnClickListener {
            it.isSelected = !it.isSelected
        }
        binding.ivItemDiscoverCourseInfoMap.setOnClickListener {
            mCallback.selectItem(data.id)
        }
    }
}