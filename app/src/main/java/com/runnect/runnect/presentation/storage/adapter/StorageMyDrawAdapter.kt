package com.runnect.runnect.presentation.storage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.databinding.ItemStorageMyDrawBinding
import com.runnect.runnect.util.callback.OnMyDrawClick


class StorageMyDrawAdapter(
    val myDrawClickListener: OnMyDrawClick
) :
    ListAdapter<ResponseGetCourseDto.Data.Course, StorageMyDrawAdapter.ItemViewHolder>(Differ()) {


    private lateinit var selectionTracker: SelectionTracker<Long>

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return currentList[position].id.toLong()
    }

    inner class ItemViewHolder(val binding: ItemStorageMyDrawBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = absoluteAdapterPosition
                override fun getSelectionKey(): Long = itemId
            }


        fun bind(courseList: ResponseGetCourseDto.Data.Course) {
            binding.storageItem = courseList
        }

    }

    fun setSelectionTracker(selectionTracker: SelectionTracker<Long>) {
        this.selectionTracker = selectionTracker //입력받은 걸 멤버변수 값에 할당, 즉 입력을 안 받으면 초기화 안 했다고 앱이 죽음
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStorageMyDrawBinding.inflate(inflater)

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])

        with(holder) {
            binding.setVariable(BR.storageItem, getItem(position))
            binding.root.setOnClickListener {
                myDrawClickListener.selectItem(currentList[position])
                selectionTracker.select(itemId) //이게 select을 실행시킴. 리사이클러뷰 아이템 고유 id 수집
            }

            binding.selected = selectionTracker.isSelected(itemId)
        }

    }


    class Differ : DiffUtil.ItemCallback<ResponseGetCourseDto.Data.Course>() {
        override fun areItemsTheSame(
            oldItem: ResponseGetCourseDto.Data.Course,
            newItem: ResponseGetCourseDto.Data.Course,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ResponseGetCourseDto.Data.Course,
            newItem: ResponseGetCourseDto.Data.Course,
        ): Boolean {
            return oldItem == newItem
        }

    }

}
