package com.runnect.runnect.presentation.discover.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverCourseBinding
import com.runnect.runnect.domain.entity.DiscoverCourse
import com.runnect.runnect.domain.entity.EditableDiscoverCourse
import com.runnect.runnect.util.callback.diff.ItemDiffCallback

class DiscoverSearchAdapter(
    private val onRecommendItemClick: (Int) -> Unit,
    private val onHeartButtonClick: (Int, Boolean) -> Unit
) : ListAdapter<DiscoverCourse, DiscoverSearchAdapter.DiscoverSearchViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverSearchViewHolder {
        return DiscoverSearchViewHolder(
            ItemDiscoverCourseBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DiscoverSearchViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class DiscoverSearchViewHolder(
        private val binding: ItemDiscoverCourseBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(course: DiscoverCourse) {
            with(binding) {
                this.course = course
                ivItemDiscoverCourseScrap.isSelected = course.scrap

                initHeartButtonClickListener(ivItemDiscoverCourseScrap, course)
                initCourseItemClickListener(root, course)
            }
        }
    }

    private fun initHeartButtonClickListener(
        imageView: AppCompatImageView,
        course: DiscoverCourse
    ) {
        imageView.setOnClickListener { view ->
            view.isSelected = !view.isSelected
            onHeartButtonClick(course.id, view.isSelected)
        }
    }

    private fun initCourseItemClickListener(
        itemView: View,
        course: DiscoverCourse
    ) {
        itemView.setOnClickListener {
            onRecommendItemClick(course.id)
        }
    }

    fun updateSearchItem(publicCourseId: Int, updatedCourse: EditableDiscoverCourse) {
        currentList.forEachIndexed { index, course ->
            if (course.id == publicCourseId) {
                course.title = updatedCourse.title
                course.scrap = updatedCourse.scrap
                notifyItemChanged(index)
                return@forEachIndexed
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverCourse>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
