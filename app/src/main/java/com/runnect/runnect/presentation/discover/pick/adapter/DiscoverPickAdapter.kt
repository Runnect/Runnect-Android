package com.runnect.runnect.presentation.discover.pick.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverPickBinding
import com.runnect.runnect.domain.entity.DiscoverUploadCourse
import com.runnect.runnect.util.callback.diff.ItemDiffCallback

class DiscoverPickAdapter(
    private val onCourseItemClick: (Boolean, DiscoverUploadCourse) -> Unit
) : ListAdapter<DiscoverUploadCourse, DiscoverPickAdapter.DiscoverPickViewHolder>(diffUtil) {
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverPickViewHolder {
        return DiscoverPickViewHolder(
            ItemDiscoverPickBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DiscoverPickViewHolder, position: Int) {
        holder.bind(currentList[position], position)
    }

    inner class DiscoverPickViewHolder(
        private val binding: ItemDiscoverPickBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: DiscoverUploadCourse, position: Int) {
            binding.course = course

            binding.ivItemDiscoverPickBackground.setOnClickListener {
                notifyItemChanged(selectedPosition) // 이전에 선택한 것은 해제
                selectedPosition = absoluteAdapterPosition
                notifyItemChanged(selectedPosition) // 새로 선택된 것은 활성화

                onCourseItemClick.invoke(selectedPosition == position, course)
            }

            // 단일 선택된 항목의 배경 활성화
            binding.ivItemDiscoverPickBackground.isSelected = (selectedPosition == position)
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverUploadCourse>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
