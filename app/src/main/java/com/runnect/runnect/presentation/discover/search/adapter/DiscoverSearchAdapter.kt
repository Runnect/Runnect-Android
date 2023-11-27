package com.runnect.runnect.presentation.discover.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverCourseBinding
import com.runnect.runnect.domain.entity.DiscoverCourse
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.callback.listener.OnHeartButtonClick
import com.runnect.runnect.util.callback.listener.OnRecommendItemClick

class DiscoverSearchAdapter(
    private val onRecommendItemClick: OnRecommendItemClick,
    private val onHeartButtonClick: OnHeartButtonClick
) : ListAdapter<DiscoverCourse, DiscoverSearchAdapter.SearchViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ItemDiscoverCourseBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class SearchViewHolder(
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
            onHeartButtonClick.scrapCourse(id = course.id, scrapTF = view.isSelected)
        }
    }

    private fun initCourseItemClickListener(
        itemView: View,
        course: DiscoverCourse
    ) {
        itemView.setOnClickListener {
            onRecommendItemClick.selectItem(publicCourseId = course.id)
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverCourse>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
