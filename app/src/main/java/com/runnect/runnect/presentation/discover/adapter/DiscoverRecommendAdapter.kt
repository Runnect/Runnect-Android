package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverRecommendBinding
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.util.callback.diff.ItemDiffCallback

class DiscoverRecommendAdapter(
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
    private val handleVisitorMode: () -> Unit,
) : ListAdapter<DiscoverMultiViewItem.RecommendCourse,
        DiscoverRecommendAdapter.DiscoverRecommendViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverRecommendViewHolder {
        return DiscoverRecommendViewHolder(
            ItemDiscoverRecommendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onHeartButtonClick,
            onCourseItemClick,
            handleVisitorMode
        )
    }

    override fun onBindViewHolder(holder: DiscoverRecommendViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class DiscoverRecommendViewHolder(
        private val binding: ItemDiscoverRecommendBinding,
        private val onHeartButtonClick: (Int, Boolean) -> Unit,
        private val onCourseItemClick: (Int) -> Unit,
        private val handleVisitorMode: () -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: DiscoverMultiViewItem.RecommendCourse) {
            with(binding) {
                this.course = course
                ivItemDiscoverCourseScrap.isSelected = course.scrap

                initHeartButtonClickListener(ivItemDiscoverCourseScrap, course)
                initCourseItemClickListener(root, course)
            }
        }

        private fun initHeartButtonClickListener(
            imageView: AppCompatImageView,
            course: DiscoverMultiViewItem.RecommendCourse
        ) {
            imageView.setOnClickListener { view ->
                if (MainActivity.isVisitorMode) {
                    handleVisitorMode.invoke()
                    return@setOnClickListener
                }
                onHeartButtonClick.invoke(course.id, !view.isSelected)
            }
        }

        private fun initCourseItemClickListener(
            itemView: View,
            course: DiscoverMultiViewItem.RecommendCourse
        ) {
            itemView.setOnClickListener {
                onCourseItemClick.invoke(course.id)
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverMultiViewItem.RecommendCourse>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}