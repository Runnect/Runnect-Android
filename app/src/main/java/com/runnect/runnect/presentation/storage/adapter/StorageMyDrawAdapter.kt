package com.runnect.runnect.presentation.storage.adapter

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.R
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.ResponseGetScrapDto
import com.runnect.runnect.databinding.ItemStorageMyDrawBinding
import com.runnect.runnect.util.callback.OnMyDrawClick
import timber.log.Timber


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
            binding.storageItem = courseList
        } //여기는 dto를 구독해서 xml 단에서 바로 ui 바인딩을 할 수 있게끔 하려는 목적인듯

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
//        holder.bindViews(currentList[position])

        with(holder) {
            binding.setVariable(BR.storageItem, getItem(position))
            binding.root.setOnClickListener {
                myDrawClickListener.selectItem(currentList[position])
                selectionTracker.select(position.toLong()) //이게 select을 실행시킴
            }
            binding.selected = selectionTracker.isSelected(position.toLong())
        }

    }

    //선택된 걸 별도의 list에다 저장하려고 애썼는데 그럴 필요가 없었음. 'selectionTracker.selection' 이 부분이 해당 역할을 이미 수행하고 있었음.
    fun removeItem(selection: Selection<Long>) {

        val itemList = mutableListOf<ResponseGetCourseDto.Data.Course>()
        itemList.addAll(currentList)
//        val selectedList = selection.toMutableList().map { it }
        val selectedList = selection.map { it.toInt() }

        Timber.tag(ContentValues.TAG)
            .d("선택한 item 인덱스 값 : ${selectedList}")

        Timber.tag(ContentValues.TAG)
            .d("삭제 전 itemList id 값: ${itemList.map { it.id }}")
//        // 인덱스를 내림차순으로 정렬
//        selectedList.sortDescending()

        for (i in selectedList) { //0,1,2
            itemList.removeAt(i)
            //보면 처음엔 [0]을 지우니까 717이 잘 삭제됐는데 이후 [1]을 삭제하려고 할 때 인덱스가 하나씩 앞으로 당겨지면서 716이 아닌 715가 삭제됨. 그래도 어떻게든 삭제는 해냈는데 [2] 때는 2개 남은
            //상황에서 [2]라는 게 애초에 존재하지 않으니 IndexOutOfBoundsException이 떠서 앱이 죽어버림.
            Timber.tag(ContentValues.TAG)
                .d("삭제 후: ${itemList.map { it.id }}")
        }


        submitList(itemList) //인덱스 0만 하나 선택해서 지우면 인덱스가 안 밀리니까 반영이 잘 돼야 할 텐데 IndexOutOfBoundsException이 뜸. 이해가 안 되네.
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
