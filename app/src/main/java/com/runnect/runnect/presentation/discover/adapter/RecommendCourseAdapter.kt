package com.runnect.runnect.presentation.discover.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.RecommendCourseDTO
import com.runnect.runnect.databinding.ItemDiscoverCourseBinding
import com.runnect.runnect.util.callback.ItemDiffCallback
import com.runnect.runnect.util.callback.OnCourseItemClicked
import com.runnect.runnect.util.callback.OnScrapClicked
import com.runnect.runnect.util.custom.toast.RunnectToast

class RecommendCourseAdapter(
    private val onScrapClicked: OnScrapClicked,
    private val onCourseItemClicked: OnCourseItemClicked,
    private val isVisitorMode: Boolean
) : ListAdapter<RecommendCourseDTO, RecommendCourseAdapter.CourseInfoViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseInfoViewHolder {
        return CourseInfoViewHolder(
            ItemDiscoverCourseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CourseInfoViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class CourseInfoViewHolder(private val binding: ItemDiscoverCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(course: RecommendCourseDTO) {
            with(binding) {
                this.course = course
                ivItemDiscoverCourseScrap.isSelected = course.scrap

                initScrapClickListener(ivItemDiscoverCourseScrap, course)
                initCourseItemClickListener(root, course)
            }
        }
    }

    private fun initScrapClickListener(
        imageView: AppCompatImageView,
        course: RecommendCourseDTO
    ) {
        imageView.setOnClickListener { view ->
            if (isVisitorMode) {
                showCourseScrapWarningToast(view.context)
                return@setOnClickListener
            }

            view.isSelected = !view.isSelected
            onScrapClicked.scrapCourse(id = course.id, scrapTF = view.isSelected)
        }
    }

    private fun initCourseItemClickListener(
        itemView: View,
        course: RecommendCourseDTO
    ) {
        itemView.setOnClickListener {
            onCourseItemClicked.selectItem(publicCourseId = course.id)
        }
    }

    private fun showCourseScrapWarningToast(context: Context) {
        RunnectToast.createToast(
            context = context,
            message = context.getString(R.string.visitor_mode_course_detail_scrap_warning_msg)
        ).show()
    }

    companion object {
        private val diffUtil = ItemDiffCallback<RecommendCourseDTO>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
