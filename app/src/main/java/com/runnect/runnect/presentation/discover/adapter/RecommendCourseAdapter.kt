package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.RecommendCourseDTO
import com.runnect.runnect.databinding.ItemDiscoverCourseBinding
import com.runnect.runnect.util.callback.ItemDiffCallback
import com.runnect.runnect.util.callback.OnCourseItemClicked
import com.runnect.runnect.util.callback.OnScrapButtonClicked
import com.runnect.runnect.util.custom.toast.RunnectToast

class RecommendCourseAdapter(
    private val onScrapButtonClicked: OnScrapButtonClicked,
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
        fun onBind(data: RecommendCourseDTO) {
            with(binding) {
                Glide.with(itemView)
                    .load(data.image)
                    .thumbnail(0.3f)
                    .format(DecodeFormat.PREFER_RGB_565).into(ivItemDiscoverCourseMap)

                tvItemDiscoverCourseTitle.text = data.title
                tvItemDiscoverCourseLocation.text = data.departure
                ivItemDiscoverCourseScrap.isSelected = data.scrap

                ivItemDiscoverCourseScrap.setOnClickListener { view ->
                    if (isVisitorMode) {
                        RunnectToast.createToast(
                            context = view.context,
                            message = view.context.getString(R.string.visitor_mode_course_detail_scrap_warning_msg)
                        ).show()
                    } else {
                        view.isSelected = !view.isSelected
                        onScrapButtonClicked.scrapCourse(id = data.id, scrapTF = view.isSelected)
                    }
                }

                root.setOnClickListener {
                    onCourseItemClicked.selectItem(publicCourseId = data.id)
                }
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<RecommendCourseDTO>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
