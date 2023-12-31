package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverMarathonBinding
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.util.callback.diff.ItemDiffCallback

class DiscoverMarathonAdapter(
    private val onHeartButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit,
    private val handleVisitorMode: () -> Unit,
) : ListAdapter<DiscoverMultiViewItem.MarathonCourse,
        DiscoverMarathonAdapter.DiscoverMarathonViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverMarathonViewHolder {
        return DiscoverMarathonViewHolder(
            ItemDiscoverMarathonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onHeartButtonClick,
            onCourseItemClick,
            handleVisitorMode
        )
    }

    override fun onBindViewHolder(holder: DiscoverMarathonViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class DiscoverMarathonViewHolder(
        private val binding: ItemDiscoverMarathonBinding,
        private val onHeartButtonClick: (Int, Boolean) -> Unit,
        private val onCourseItemClick: (Int) -> Unit,
        private val handleVisitorMode: () -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: DiscoverMultiViewItem.MarathonCourse) {
            with(binding) {
                this.course = course
                ivItemDiscoverCourseScrap.isSelected = course.scrap

                initHeartButtonClickListener(ivItemDiscoverCourseScrap, course)
                initCourseItemClickListener(root, course)
            }
        }

        private fun initHeartButtonClickListener(
            imageView: AppCompatImageView,
            course: DiscoverMultiViewItem.MarathonCourse
        ) {
            imageView.setOnClickListener { view ->
                if (MainActivity.isVisitorMode) {
                    handleVisitorMode.invoke()
                    return@setOnClickListener
                }

                view.isSelected = !view.isSelected
                onHeartButtonClick.invoke(course.id, view.isSelected)
            }
        }

        private fun initCourseItemClickListener(
            itemView: View,
            course: DiscoverMultiViewItem.MarathonCourse
        ) {
            itemView.setOnClickListener {
                onCourseItemClick.invoke(course.id)
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverMultiViewItem.MarathonCourse>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
