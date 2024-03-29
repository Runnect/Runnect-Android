package com.runnect.runnect.presentation.discover.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverSearchBinding
import com.runnect.runnect.domain.entity.DiscoverSearchCourse
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import com.runnect.runnect.util.callback.diff.ItemDiffCallback

class DiscoverSearchAdapter(
    private val onRecommendItemClick: (Int) -> Unit,
    private val onHeartButtonClick: (Int, Boolean) -> Unit
) : ListAdapter<DiscoverSearchCourse, DiscoverSearchAdapter.DiscoverSearchViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverSearchViewHolder {
        return DiscoverSearchViewHolder(
            ItemDiscoverSearchBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DiscoverSearchViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class DiscoverSearchViewHolder(
        private val binding: ItemDiscoverSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: DiscoverSearchCourse) {
            with(binding) {
                this.course = course
                ivItemDiscoverCourseScrap.isSelected = course.scrap

                initHeartButtonClickListener(ivItemDiscoverCourseScrap, course)
                initCourseItemClickListener(root, course)
            }
        }

        private fun initHeartButtonClickListener(
            imageView: AppCompatImageView,
            course: DiscoverSearchCourse
        ) {
            imageView.setOnClickListener { view ->
                onHeartButtonClick(course.id, !view.isSelected)
            }
        }

        private fun initCourseItemClickListener(
            itemView: View,
            course: DiscoverSearchCourse
        ) {
            itemView.setOnClickListener {
                onRecommendItemClick(course.id)
            }
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

    fun updateCourseScrap(
        publicCourseId: Int,
        scrap: Boolean
    ) {
        currentList.forEachIndexed { index, course ->
            if (course.id == publicCourseId) {
                course.scrap = scrap
                notifyItemChanged(index)
                return@forEachIndexed
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverSearchCourse>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
