package com.runnect.runnect.presentation.discover.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.R
import com.runnect.runnect.databinding.ItemDiscoverCourseBinding
import com.runnect.runnect.domain.entity.DiscoverCourse
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.callback.listener.OnRecommendItemClick
import com.runnect.runnect.util.callback.listener.OnHeartButtonClick
import com.runnect.runnect.util.custom.toast.RunnectToast

class DiscoverRecommendAdapter(
    private val onHeartButtonClick: OnHeartButtonClick,
    private val onRecommendItemClick: OnRecommendItemClick,
    private val isVisitorMode: Boolean
) : ListAdapter<DiscoverCourse, DiscoverRecommendAdapter.CourseInfoViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseInfoViewHolder {
        return CourseInfoViewHolder(
            ItemDiscoverCourseBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CourseInfoViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class CourseInfoViewHolder(
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
            if (isVisitorMode) {
                showCourseScrapWarningToast(view.context)
                return@setOnClickListener
            }

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

    private fun showCourseScrapWarningToast(context: Context) {
        RunnectToast.createToast(
            context = context,
            message = context.getString(R.string.visitor_mode_course_detail_scrap_warning_msg)
        ).show()
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverCourse>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
