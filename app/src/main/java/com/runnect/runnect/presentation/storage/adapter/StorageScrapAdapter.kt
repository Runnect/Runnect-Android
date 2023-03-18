package com.runnect.runnect.presentation.storage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.model.ResponseGetScrapDto
import com.runnect.runnect.databinding.ItemStorageScrapBinding
import com.runnect.runnect.util.callback.ItemCount
import com.runnect.runnect.util.callback.OnHeartClick
import com.runnect.runnect.util.callback.OnScrapCourseClick
import timber.log.Timber

class StorageScrapAdapter(
    val scrapClickListener: OnScrapCourseClick,
    val heartListener: OnHeartClick, val itemCount: ItemCount
) :
    ListAdapter<ResponseGetScrapDto.Data.Scrap, StorageScrapAdapter.ItemViewHolder>(Differ()) {


    inner class ItemViewHolder(val binding: ItemStorageScrapBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: ResponseGetScrapDto.Data.Scrap) {
            with(binding) {
                ivItemStorageScrapHeart.isSelected = true
                ivItemStorageScrapHeart.setOnClickListener {
                    ivItemStorageScrapHeart.isSelected = false
                    deleteItem(adapterPosition)
                    heartListener.scrapCourse(data.publicCourseId, it.isSelected)
                }

                root.setOnClickListener {
                    scrapClickListener.selectItem(data)
                }
            }
        }

        fun bind(scrapList: ResponseGetScrapDto.Data.Scrap) {
            binding.storageScrap = scrapList
        }

    }

    fun deleteItem(position: Int){
        val itemList = mutableListOf<ResponseGetScrapDto.Data.Scrap>()
        itemList.addAll(currentList)
        Timber.d("삭제 전 itemList? ${itemList.size}")
        itemList.removeAt(position)
        submitList(itemList)
        Timber.d("삭제 후 itemList? ${itemList.size}")
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


    class Differ : DiffUtil.ItemCallback<ResponseGetScrapDto.Data.Scrap>() {
        override fun areItemsTheSame(
            oldItem: ResponseGetScrapDto.Data.Scrap,
            newItem: ResponseGetScrapDto.Data.Scrap,
        ): Boolean {
            return oldItem.courseId == newItem.courseId
        }

        override fun areContentsTheSame(
            oldItem: ResponseGetScrapDto.Data.Scrap,
            newItem: ResponseGetScrapDto.Data.Scrap,
        ): Boolean {
            return oldItem == newItem
        }

    }
}
