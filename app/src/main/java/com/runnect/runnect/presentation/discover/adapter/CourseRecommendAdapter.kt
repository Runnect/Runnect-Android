package com.runnect.runnect.presentation.discover.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.load
import com.runnect.runnect.data.model.CourseInfoDTO
import com.runnect.runnect.databinding.ItemCourseInfoBinding
import com.runnect.runnect.util.DiffUtilItemCallback

class CourseRecommendAdapter(context: Context) :
    ListAdapter<CourseInfoDTO, CourseRecommendAdapter.CourseInfoViewHolder>(DiffUtilItemCallback()) {
    private val inflater by lazy { LayoutInflater.from(context) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseInfoViewHolder {
        return CourseInfoViewHolder(
            ItemCourseInfoBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CourseInfoViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class CourseInfoViewHolder(private val binding: ItemCourseInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data:CourseInfoDTO) {
            binding.ivItemCourseInfoMap.load(data.img)
            binding.tvItemCourseInfoTitle.text = data.title
            binding.tvItemCourseInfoLocation.text = data.location
            binding.ivItemCourseInfoFavorite.isSelected = data.isFavorite
        }
    }

}
