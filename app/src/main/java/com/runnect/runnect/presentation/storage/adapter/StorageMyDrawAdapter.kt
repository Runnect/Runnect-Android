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


class StorageMyDrawAdapter(val myDrawClickListener: OnMyDrawClick) :
    ListAdapter<ResponseGetCourseDto.Data.Course, StorageMyDrawAdapter.ItemViewHolder>(Differ()) {


    private lateinit var selectionTracker: SelectionTracker<Long>

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ItemViewHolder(val binding: ItemStorageMyDrawBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = absoluteAdapterPosition
                override fun getSelectionKey(): Long = itemId
            }


        fun bind(courseList: ResponseGetCourseDto.Data.Course) {
            binding.storage = courseList
        } //여기는 dto를 구독해서 xml 단에서 바로 ui 바인딩을 할 수 있게끔 하려는 목적인듯

        // 여기는 item이 터치가 됐을 때 동작을 만들어주기 위함.
        // 이 listener를 인터페이스로 만들고 추상메서드 override할 때 뷰모델에 세팅해주고
        // 그 뷰모델에 있는 liveData를 xml에서 구독하게 하면 굳이 이렇게 함수 2개로 안 나눠도 될듯
        fun bindViews(data: ResponseGetCourseDto.Data.Course) {
            binding.root.setOnClickListener {
                myDrawClickListener.selectItem(data)
//                selectionTracker.select(itemPosition.toLong())
            }
        }

    }

    fun setSelectionTracker(selectionTracker: SelectionTracker<Long>) {
        this.selectionTracker = selectionTracker
    }

    //삭제 함수도 넣어야지, Scarp에 만들어뒀던 거랑 합치기
//    fun removeItem(selection: Selection<Long>) {
//        val currentList = currentList.toMutableList()
//        val itemList = mutableListOf<GitData>()
//        selection.forEach {
//            itemList.add(currentList[it.toInt()])
//        }
//        currentList.removeAll(itemList)
//        submitList(currentList)
//    }

//    fun setSelectionTracker(selectionTracker: SelectionTracker<Long>?) {
//        if (selectionTracker != null && selectionTracker.isSelected(getAdapterPosition() as Long)) {
//            checkBox.setImageResource(R.drawable.checkbox_on_background)
//        } else {
//            checkBox.setImageResource(R.drawable.checkbox_off_background)
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStorageMyDrawBinding.inflate(inflater)

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.bindViews(currentList[position])

        //근데 이걸 onBindViewHolder에 적어주는 게 맞나?
//        with(holder as ItemViewHolder) {
//            binding.setVariable(BR.storage, getItem(position) as GitData)
//            binding.root.setOnClickListener {
//                selectionTracker.select(position.toLong())
//            }
//            binding.selected = selectionTracker.isSelected(position.toLong()) }
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
