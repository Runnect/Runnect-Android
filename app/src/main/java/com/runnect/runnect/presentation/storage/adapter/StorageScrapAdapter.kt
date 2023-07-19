package com.runnect.runnect.presentation.storage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.dto.MyScrapCourse
import com.runnect.runnect.databinding.ItemStorageScrapBinding
import com.runnect.runnect.util.callback.ItemCount
import com.runnect.runnect.util.callback.OnHeartClick
import com.runnect.runnect.util.callback.OnScrapCourseClick

class StorageScrapAdapter(
    val scrapClickListener: OnScrapCourseClick,
    val heartListener: OnHeartClick, val itemCount: ItemCount
) :
    ListAdapter<MyScrapCourse, StorageScrapAdapter.ItemViewHolder>(Differ()) {


    inner class ItemViewHolder(val binding: ItemStorageScrapBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: MyScrapCourse) {
            with(binding) {
                ivItemStorageScrapHeart.isSelected = true
                ivItemStorageScrapHeart.setOnClickListener {
                    ivItemStorageScrapHeart.isSelected = false
                    deleteItem(absoluteAdapterPosition)
                    heartListener.scrapCourse(data.publicCourseId, it.isSelected)
                }

                root.setOnClickListener {
                    scrapClickListener.selectItem(data)
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


    class Differ : DiffUtil.ItemCallback<MyScrapCourse>() {
        override fun areItemsTheSame(
            oldItem: MyScrapCourse,
            newItem: MyScrapCourse,
        ): Boolean {
            return oldItem.publicCourseId == newItem.publicCourseId
        }

        override fun areContentsTheSame(
            oldItem: MyScrapCourse,
            newItem: MyScrapCourse,
        ): Boolean {
            return oldItem == newItem
        }

    }
}
