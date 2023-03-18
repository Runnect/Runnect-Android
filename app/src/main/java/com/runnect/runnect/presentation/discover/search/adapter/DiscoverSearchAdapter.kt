package com.runnect.runnect.presentation.discover.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.databinding.ItemDiscoverCourseInfoBinding
import com.runnect.runnect.util.CourseSearchDiffUtilItemCallback
import com.runnect.runnect.util.callback.OnItemClick
import com.runnect.runnect.util.callback.OnHeartClick

class DiscoverSearchAdapter(context: Context, listener: OnItemClick,scrapListener:OnHeartClick) :
    ListAdapter<CourseSearchDTO, SearchViewHolder>(
        CourseSearchDiffUtilItemCallback()
    ) {
    private val inflater by lazy { LayoutInflater.from(context) }
    private val mCallback = listener
    private val sCallback = scrapListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ItemDiscoverCourseInfoBinding.inflate(inflater, parent, false),
            mCallback,sCallback
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }
}

class SearchViewHolder(
    private val binding: ItemDiscoverCourseInfoBinding,
    private val mCallback: OnItemClick,
    private val sCallback: OnHeartClick
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(data: CourseSearchDTO) {
        binding.ivItemDiscoverCourseInfoMap.load(data.image)
        binding.tvItemDiscoverCourseInfoTitle.text = data.title
        binding.tvItemDiscoverCourseInfoLocation.text = data.departure
        binding.ivItemDiscoverCourseInfoScrap.isSelected = data.scrap
        binding.ivItemDiscoverCourseInfoScrap.setOnClickListener {
            it.isSelected = !it.isSelected
            sCallback.scrapCourse(data.id,it.isSelected)
        }
        binding.ivItemDiscoverCourseInfoMap.setOnClickListener {
            mCallback.selectItem(data.id)
        }
    }
}