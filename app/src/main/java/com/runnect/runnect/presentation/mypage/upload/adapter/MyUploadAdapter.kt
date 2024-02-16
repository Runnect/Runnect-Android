package com.runnect.runnect.presentation.mypage.upload.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.databinding.ItemMypageUploadBinding
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.callback.listener.OnMyUploadItemClick

class MyUploadAdapter(
    private val onMyUploadItemClick: OnMyUploadItemClick
) : ListAdapter<UserUploadCourseDTO, MyUploadAdapter.MyUploadViewHolder>(diffUtil) {
    private var selectedItems: MutableList<View>? = mutableListOf()
    private var selectedBoxes: MutableList<View>? = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyUploadViewHolder {
        return MyUploadViewHolder(
            ItemMypageUploadBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyUploadViewHolder, position: Int) {
        holder.onBind(currentList[position])
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
            val newItems = currentList.filter { !removedIds.contains(it.id) }
            submitList(newItems)
        }
    }

    inner class MyUploadViewHolder(private val binding: ItemMypageUploadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: UserUploadCourseDTO) {
            with(binding) {
                selectedBoxes?.add(ivCheckbox)

                Glide.with(itemView)
                    .load(data.img)
                    .thumbnail(0.3f)
                    .format(DecodeFormat.PREFER_RGB_565).into(ivMyPageUploadCourse)

                tvMyPageUploadCourseTitle.text = data.title
                tvMyPageUploadCourseLocation.text = data.departure

                ivMyPageUploadCourseSelectBackground.setOnClickListener {
                    val isEditMode = onMyUploadItemClick.selectItem(data.id)
                    if (isEditMode) {
                        if (it.isSelected) {
                            ivCheckbox.isSelected = false
                            it.isSelected = false
                            selectedBoxes?.remove(ivCheckbox)
                            selectedItems?.remove(it)
                        } else {
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

    fun updateMyUploadItem(publicCourseId: Int, updatedCourse: EditableDiscoverCourse) {
        currentList.forEachIndexed { index, course ->
            if (course.id == publicCourseId) {
                course.title = updatedCourse.title
                notifyItemChanged(index)
                return@forEachIndexed
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<UserUploadCourseDTO>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
