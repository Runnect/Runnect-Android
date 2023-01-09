package com.runnect.runnect.presentation.mypage.upload.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.model.UploadedCourseDTO
import com.runnect.runnect.databinding.ItemMypageUploadBinding
import com.runnect.runnect.util.CourseUploadedDiffUtilItemCallback

class MyUploadAdapter(context: Context) :
    ListAdapter<UploadedCourseDTO, MyUploadViewHolder>(
        CourseUploadedDiffUtilItemCallback()
    ) {
    private val inflater by lazy { LayoutInflater.from(context) }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyUploadViewHolder {
        return MyUploadViewHolder(ItemMypageUploadBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MyUploadViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }
}

class MyUploadViewHolder(private val binding: ItemMypageUploadBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(data: UploadedCourseDTO) {
        binding.ivMyPageUploadCourse.setImageResource(data.img)
        binding.tvMyPageUploadCourseLocation.text = data.location
    }
}