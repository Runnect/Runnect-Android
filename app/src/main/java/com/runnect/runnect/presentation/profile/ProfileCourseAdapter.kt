package com.runnect.runnect.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.dto.ProfileCourseData
import com.runnect.runnect.databinding.ItemProfileCourseBinding
import com.runnect.runnect.util.callback.diff.ItemDiffCallback

class ProfileCourseAdapter(
) : ListAdapter<ProfileCourseData, ProfileCourseAdapter.UploadedCourseViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadedCourseViewHolder {
        return UploadedCourseViewHolder(
            ItemProfileCourseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UploadedCourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UploadedCourseViewHolder(
        private val binding: ItemProfileCourseBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(profileCourseData: ProfileCourseData) {
            with(binding) {
                data = profileCourseData
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<ProfileCourseData>(
            onItemsTheSame = { old, new -> old.courseId == new.courseId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
