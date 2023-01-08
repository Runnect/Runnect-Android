package com.runnect.runnect.presentation.discover.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.runnect.runnect.data.model.CourseInfoDTO
import com.runnect.runnect.databinding.ItemDiscoverCourseInfoBinding
import com.runnect.runnect.util.DiffUtilItemCallback

class CourseRecommendAdapter(context: Context) :
    ListAdapter<CourseInfoDTO, CourseRecommendAdapter.CourseInfoViewHolder>(DiffUtilItemCallback()) {
    private val inflater by lazy { LayoutInflater.from(context) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseInfoViewHolder {
        return CourseInfoViewHolder(
            ItemDiscoverCourseInfoBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CourseInfoViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class CourseInfoViewHolder(private val binding: ItemDiscoverCourseInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: CourseInfoDTO) {
            binding.ivItemDiscoverCourseInfoMap.load(data.img)
            binding.tvItemDiscoverCourseInfoTitle.text = data.title
            binding.tvItemDiscoverCourseInfoLocation.text = data.location
            binding.ivItemDiscoverCourseInfoScrap.isSelected = data.isScraped
            binding.ivItemDiscoverCourseInfoScrap.setOnClickListener {
                it.isSelected = !it.isSelected
            }
        }
    }

}
