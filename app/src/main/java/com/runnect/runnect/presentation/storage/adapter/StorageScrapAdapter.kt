package com.runnect.runnect.presentation.storage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.model.ResponseGetScrapDto
import com.runnect.runnect.databinding.ItemStorageScrapBinding

class StorageScrapAdapter(scrapClickListener: (ResponseGetScrapDto.Data.Scrap) -> Unit) :
    ListAdapter<ResponseGetScrapDto.Data.Scrap, StorageScrapAdapter.ItemViewHolder>(Differ()) {


    private val listener = scrapClickListener

    inner class ItemViewHolder(val binding: ItemStorageScrapBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(scrapList: ResponseGetScrapDto.Data.Scrap) {
            binding.storageScrap = scrapList
        }

        //클릭 이벤트 구현부
        fun bindViews(data: ResponseGetScrapDto.Data.Scrap) {
            binding.root.setOnClickListener {
                listener(data)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStorageScrapBinding.inflate(inflater)

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.bindViews(currentList[position])
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
