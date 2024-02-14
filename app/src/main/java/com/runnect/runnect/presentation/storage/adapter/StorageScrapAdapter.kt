package com.runnect.runnect.presentation.storage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.databinding.ItemStorageScrapBinding
import com.runnect.runnect.util.callback.ItemCount
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.callback.listener.OnHeartButtonClick
import com.runnect.runnect.util.callback.listener.OnScrapItemClick

class StorageScrapAdapter(
    private val onScrapItemClick: OnScrapItemClick,
    private val onHeartButtonClick: OnHeartButtonClick,
    private val itemCount: ItemCount
) : ListAdapter<MyScrapCourse, StorageScrapAdapter.ItemViewHolder>(diffUtil) {
    private var clickedItemPosition = -1

    inner class ItemViewHolder(val binding: ItemStorageScrapBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(course: MyScrapCourse) {
            binding.storageScrap = course
            binding.ivItemStorageScrapHeart.isSelected = true

            initCourseItemClickListener(binding.root, course)
            initHeartButtonClickListener(binding.ivItemStorageScrapHeart, course)
        }

        private fun initCourseItemClickListener(view: View, course: MyScrapCourse) {
            view.setOnClickListener {
                onScrapItemClick.selectItem(course)
            }
        }

        private fun initHeartButtonClickListener(view: ImageView, course: MyScrapCourse) {
            view.setOnClickListener {
                clickedItemPosition = absoluteAdapterPosition
                onHeartButtonClick.scrapCourse(course.publicCourseId, !it.isSelected)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStorageScrapBinding.inflate(inflater)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    fun removeCourseItem() {
        if (clickedItemPosition == -1) return
        val newList = currentList.toMutableList()
        newList.removeAt(clickedItemPosition)
        submitList(newList)
        itemCount.calcItemSize(newList.size)
    }

    companion object {
        private val diffUtil = ItemDiffCallback<MyScrapCourse>(
            onItemsTheSame = { old, new -> old.publicCourseId == new.publicCourseId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
