package com.runnect.runnect.presentation.storage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.entity.SearchResultEntity
import com.runnect.runnect.databinding.ItemStorageBinding

class StorageAdapter(courseClickListener: (ResponseGetCourseDto.Data.Course) -> Unit) :
    ListAdapter<ResponseGetCourseDto.Data.Course, StorageAdapter.ItemViewHolder>(Differ()) {


    private val listener = courseClickListener

    inner class ItemViewHolder(val binding: ItemStorageBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(courseList: ResponseGetCourseDto.Data.Course) {
            binding.storage = courseList
        }

        //클릭 이벤트 구현부
        fun bindViews(data: ResponseGetCourseDto.Data.Course) {
            binding.root.setOnClickListener {
                listener(data)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStorageBinding.inflate(inflater)

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.bindViews(currentList[position])
    }


    class Differ : DiffUtil.ItemCallback<ResponseGetCourseDto.Data.Course>() {
        override fun areItemsTheSame(
            oldItem: ResponseGetCourseDto.Data.Course,
            newItem: ResponseGetCourseDto.Data.Course,
        ): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(
            oldItem: ResponseGetCourseDto.Data.Course,
            newItem: ResponseGetCourseDto.Data.Course,
        ): Boolean {
            return oldItem == newItem
        }

    }
}
