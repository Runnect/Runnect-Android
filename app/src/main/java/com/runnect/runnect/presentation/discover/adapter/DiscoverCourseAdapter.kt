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
import com.runnect.runnect.domain.entity.EditableDiscoverCourse
import com.runnect.runnect.presentation.MainActivity.Companion.isVisitorMode
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.custom.toast.RunnectToast

class DiscoverCourseAdapter(
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
) : ListAdapter<DiscoverCourse, DiscoverCourseAdapter.DiscoverCourseViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverCourseViewHolder {
        return DiscoverCourseViewHolder(
            ItemDiscoverCourseBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DiscoverCourseViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class DiscoverCourseViewHolder(
        private val binding: ItemDiscoverCourseBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: DiscoverCourse) {
            with(binding) {
                this.course = course
                ivItemDiscoverCourseScrap.isSelected = course.scrap

                initHeartButtonClickListener(ivItemDiscoverCourseScrap, course)
                initCourseItemClickListener(root, course)
            }
        }

        private fun initCourseItemClickListener(
            itemView: View,
            course: DiscoverCourse
        ) {
            itemView.setOnClickListener {
                onCourseItemClick(course.id)
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
                onHeartButtonClick(course.id, view.isSelected)
            }
        }

        private fun showCourseScrapWarningToast(context: Context) {
            RunnectToast.createToast(
                context = context,
                message = context.getString(R.string.visitor_mode_course_detail_scrap_warning_msg)
            ).show()
        }
    }

    fun updateRecommendItem(publicCourseId: Int, updatedCourse: EditableDiscoverCourse) {
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
