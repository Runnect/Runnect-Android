package com.runnect.runnect.presentation.mypage.upload.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.databinding.ItemMypageUploadBinding
import com.runnect.runnect.util.CourseUploadedDiffUtilItemCallback
import com.runnect.runnect.util.callback.OnUploadItemClick

class MyUploadAdapter(context: Context, val listener: OnUploadItemClick) :
    ListAdapter<UserUploadCourseDTO, MyUploadAdapter.MyUploadViewHolder>(
        CourseUploadedDiffUtilItemCallback()
    ) {
    private val inflater by lazy { LayoutInflater.from(context) }
    private var selectedItems: MutableList<View>? = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyUploadViewHolder {
        return MyUploadViewHolder(ItemMypageUploadBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MyUploadViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    fun clearSelection() {
        selectedItems?.forEach {
            it.isSelected = false
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
                Glide.with(itemView).load(data.img).thumbnail(0.3f)
                    .format(DecodeFormat.PREFER_RGB_565).into(ivMyPageUploadCourse)
                tvMyPageUploadCourseTitle.text = data.title
                tvMyPageUploadCourseLocation.text = data.departure
                ivMyPageUploadCourseSelectBackground.setOnClickListener {
                    val isEditMode = listener.selectItem(data.id)
                    if(isEditMode){
                        if(it.isSelected){
                            it.isSelected = false
                            selectedItems?.remove(it)
                        }
                        else{
                            it.isSelected = true
                            selectedItems?.add(it)
                        }
                    }
                }
            }
        }
    }
}

