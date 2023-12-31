package com.runnect.runnect.presentation.storage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.dto.MyDrawCourse
import com.runnect.runnect.databinding.ItemStorageMyDrawBinding
import com.runnect.runnect.util.callback.ItemCount
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.callback.listener.OnMyDrawItemClick
import timber.log.Timber

class StorageMyDrawAdapter(
    private val onMyDrawItemClick: OnMyDrawItemClick,
    private val itemCount: ItemCount
) : ListAdapter<MyDrawCourse, StorageMyDrawAdapter.ItemViewHolder>(diffUtil) {
    lateinit var binding: ItemStorageMyDrawBinding
    private var selectedItems: MutableList<View>? = mutableListOf()
    private var selectedBoxes: MutableList<View>? = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemStorageMyDrawBinding.inflate(inflater)

        return ItemViewHolder(binding)
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

    inner class ItemViewHolder(val binding: ItemStorageMyDrawBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(courseList: MyDrawCourse) {
            with(binding) {
                selectedBoxes?.add(ivCheckbox)

                storageItem = courseList

                ivMyPageUploadCourseSelectBackground.setOnClickListener {
                    val isEditMode = onMyDrawItemClick.selectItem(courseList.courseId!!)
                    Timber.d("isEditMode값 : $isEditMode")
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

    companion object {
        private val diffUtil = ItemDiffCallback<MyDrawCourse>(
            onItemsTheSame = { old, new -> old.courseId == new.courseId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
