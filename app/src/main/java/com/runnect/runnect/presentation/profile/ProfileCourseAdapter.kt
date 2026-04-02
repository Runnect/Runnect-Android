package com.runnect.runnect.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.domain.entity.UserCourse
import com.runnect.runnect.databinding.ItemProfileCourseBinding
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.extension.setOnSingleClickListener

class ProfileCourseAdapter(
    private val onScrapButtonClick: (Int, Boolean) -> Unit,
    private val onCourseItemClick: (Int) -> Unit
) : ListAdapter<UserCourse, ProfileCourseAdapter.UploadedCourseViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadedCourseViewHolder {
        return UploadedCourseViewHolder(
            ItemProfileCourseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onScrapButtonClick = onScrapButtonClick,
            onCourseItemClick = onCourseItemClick
        )
    }

    override fun onBindViewHolder(holder: UploadedCourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UploadedCourseViewHolder(
        private val binding: ItemProfileCourseBinding,
        private val onScrapButtonClick: (Int, Boolean) -> Unit,
        private val onCourseItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userCourse: UserCourse) {
            with(binding) {
                data = userCourse
                ivItemProfileCourseHeart.setOnSingleClickListener {
                    onScrapButtonClick(userCourse.publicCourseId, !userCourse.scrapTF)
                }

                clItemProfileCourse.setOnSingleClickListener {
                    onCourseItemClick(userCourse.publicCourseId)
                }
            }
        }
    }

    fun updateCourseItem(courseId: Int, scrapTF: Boolean) {
        currentList.forEachIndexed { index, userCourseData ->
            if (userCourseData.publicCourseId == courseId) {
                userCourseData.scrapTF = scrapTF
                notifyItemChanged(index)
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<UserCourse>(
            onItemsTheSame = { old, new -> old.courseId == new.courseId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
