package com.runnect.runnect.presentation.discover.pick.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverPickBinding
import com.runnect.runnect.domain.entity.DiscoverUploadCourse
import com.runnect.runnect.util.callback.diff.ItemDiffCallback

class DiscoverPickAdapter(
    private val onCourseItemClick: (Boolean, DiscoverUploadCourse) -> Unit
) : ListAdapter<DiscoverUploadCourse, DiscoverPickAdapter.DiscoverPickViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverPickViewHolder {
        return DiscoverPickViewHolder(
            binding = ItemDiscoverPickBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onCourseItemClick = onCourseItemClick
        )
    }

    override fun onBindViewHolder(holder: DiscoverPickViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class DiscoverPickViewHolder(
        private val binding: ItemDiscoverPickBinding,
        private val onCourseItemClick: (Boolean, DiscoverUploadCourse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: DiscoverUploadCourse) {
            binding.course = course
            binding.ivItemDiscoverPickBackground.isSelected = false

            binding.ivItemDiscoverPickBackground.setOnClickListener { view ->
                view.isSelected = !view.isSelected
                onCourseItemClick.invoke(view.isSelected, course)
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverUploadCourse>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
