package com.runnect.runnect.presentation.storage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.domain.entity.MyDrawCourse
import com.runnect.runnect.databinding.ItemStorageMyDrawBinding
import com.runnect.runnect.util.callback.ItemCount
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.callback.listener.OnMyDrawItemClick

class StorageMyDrawAdapter(
    private val onMyDrawItemClick: OnMyDrawItemClick,
    private val itemCount: ItemCount
) : ListAdapter<MyDrawCourse, StorageMyDrawAdapter.ItemViewHolder>(diffUtil) {
    private var selectedItems: MutableList<View>? = mutableListOf()
    private var selectedBoxes: MutableList<View>? = mutableListOf()

    inner class ItemViewHolder(val binding: ItemStorageMyDrawBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(course: MyDrawCourse) {
            with(binding) {
                selectedBoxes?.add(ivCheckbox)
                storageItem = course

                ivMyPageUploadCourseSelectBackground.setOnClickListener {
                    val isEditMode = onMyDrawItemClick.selectItem(
                        id = course.courseId ?: 0,
                        title = course.title
                    )

                    if (isEditMode) {
                        if (it.isSelected) {
                            ivCheckbox.isSelected = false
                            it.isSelected = false
                            selectedBoxes?.remove(ivCheckbox)
                            selectedItems?.remove(it)
                        } else {
                            selected = true
                            ivCheckbox.isSelected = true
                            it.isSelected = true
                            selectedBoxes?.add(ivCheckbox)
                            selectedItems?.add(it)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            binding = ItemStorageMyDrawBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    fun handleCheckBoxVisibility(isEditMode: Boolean) {
        selectedBoxes?.forEach {
            it.isVisible = isEditMode
        }
    }

    fun clearSelection() {
        selectedItems?.forEach {
            it.isSelected = false
        }
        selectedBoxes?.forEach {
            it.isSelected = false
            it.isVisible = false
        }
        selectedItems?.clear()
    }

    fun removeItems(removedIds: List<Int>) {
        if (currentList.isNotEmpty()) {
            val newItems = currentList.filter { !removedIds.contains(it.courseId) }
            submitList(newItems)
            itemCount.calcItemSize(newItems.size)
        }
    }

    fun updateCourseTitle(courseId: Int, updatedTitle: String) {
        currentList.forEachIndexed { index, course ->
            if (course.courseId == courseId) {
                course.title = updatedTitle
                notifyItemChanged(index)
                return@forEachIndexed
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<MyDrawCourse>(
            onItemsTheSame = { old, new -> old.courseId == new.courseId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
