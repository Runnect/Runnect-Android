package com.runnect.runnect.presentation.storage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.dto.MyDrawCourse
import com.runnect.runnect.data.dto.MyScrapCourse
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
    inner class ItemViewHolder(val binding: ItemStorageScrapBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: MyScrapCourse) {
            with(binding) {
                ivItemStorageScrapHeart.isSelected = true
                ivItemStorageScrapHeart.setOnClickListener {
                    ivItemStorageScrapHeart.isSelected = false
                    deleteItem(absoluteAdapterPosition)
                    onHeartButtonClick.scrapCourse(data.publicCourseId, it.isSelected)
                }

                root.setOnClickListener {
                    onScrapItemClick.selectItem(data)
                }
            }
        }
        fun bind(scrapList: MyScrapCourse) {
            binding.storageScrap = scrapList
        }
    }

    fun deleteItem(position: Int) {
        val itemList = mutableListOf<MyScrapCourse>()
        itemList.addAll(currentList)
        itemList.removeAt(position)
        submitList(itemList)
        itemCount.calcItemSize(itemList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStorageScrapBinding.inflate(inflater)

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.onBind(currentList[position])
    }

    companion object {
        private val diffUtil = ItemDiffCallback<MyScrapCourse>(
            onItemsTheSame = { old, new -> old.publicCourseId == new.publicCourseId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
