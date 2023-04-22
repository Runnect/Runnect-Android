package com.runnect.runnect.presentation.storage.adapter

import android.content.ContentValues
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.model.RequestPutMyDrawDto
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.databinding.ItemStorageMyDrawBinding
import com.runnect.runnect.util.callback.DeleteMyDrawCourse
import com.runnect.runnect.util.callback.OnMyDrawClick
import timber.log.Timber


class StorageMyDrawAdapter(
    val myDrawClickListener: OnMyDrawClick,
    val deleteCourseListener: DeleteMyDrawCourse
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
            Timber.tag(ContentValues.TAG)
                .d("selection 값 : ${selectionTracker.selection}")
            Timber.tag(ContentValues.TAG)
                .d("getItemId 값 : $itemId")
            binding.selected = selectionTracker.isSelected(itemId)
        }

    }

    fun removeItem(selection: Selection<Long>) {
        val itemList = currentList.toMutableList()
        val selectedIds = selection.toMutableList()

        Timber.tag(ContentValues.TAG)
            .d("selectedIds 값 : $selectedIds")

        //화면에서 지우는 거랑 이걸 서버에 반영해주는 거랑 별개야
        //이렇게 하면 화면에선 지운 거고 인터페이스로
        for (id in selectedIds) {
            val index = itemList.indexOfFirst { it.id.toLong() == id.toLong() }
            if (index != -1) {
                itemList.removeAt(index)
            }
        }
        submitList(itemList)

        deleteCourseListener.deleteCourse(selectedIds)
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
