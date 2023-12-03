package com.runnect.runnect.presentation.discover.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.R
import com.runnect.runnect.databinding.ItemDiscoverMarathonBinding
import com.runnect.runnect.databinding.ItemDiscoverRecommendBinding
import com.runnect.runnect.domain.entity.DiscoverMultiItem
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.custom.toast.RunnectToast

class DiscoverRecommendAdapter(
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
) : ListAdapter<DiscoverMultiItem.RecommendCourse,
        DiscoverRecommendAdapter.DiscoverRecommendViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverRecommendViewHolder {
        return DiscoverRecommendViewHolder(
            ItemDiscoverRecommendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onHeartButtonClick,
            onCourseItemClick
        )
    }

    override fun onBindViewHolder(holder: DiscoverRecommendViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class DiscoverRecommendViewHolder(
        private val binding: ItemDiscoverRecommendBinding,
        private val onHeartButtonClick: (Int, Boolean) -> Unit,
        private val onCourseItemClick: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: DiscoverMultiItem.RecommendCourse) {
            with(binding) {
                this.course = course
                ivItemDiscoverCourseScrap.isSelected = course.scrap

                initHeartButtonClickListener(ivItemDiscoverCourseScrap, course)
                initCourseItemClickListener(root, course)
            }
        }

        private fun initHeartButtonClickListener(
            imageView: AppCompatImageView,
            course: DiscoverMultiItem.RecommendCourse
        ) {
            imageView.setOnClickListener { view ->
                if (MainActivity.isVisitorMode) {
                    showCourseScrapWarningToast(view.context)
                    return@setOnClickListener
                }

                view.isSelected = !view.isSelected
                onHeartButtonClick(course.id, view.isSelected)
            }
        }

        private fun initCourseItemClickListener(
            itemView: View,
            course: DiscoverMultiItem.RecommendCourse
        ) {
            itemView.setOnClickListener {
                onCourseItemClick(course.id)
            }
        }

        private fun showCourseScrapWarningToast(context: Context) {
            RunnectToast.createToast(
                context = context,
                message = context.getString(R.string.visitor_mode_course_detail_scrap_warning_msg)
            ).show()
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverMultiItem.RecommendCourse>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}