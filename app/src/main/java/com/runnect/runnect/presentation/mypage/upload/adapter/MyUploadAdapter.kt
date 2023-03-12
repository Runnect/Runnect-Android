package com.runnect.runnect.presentation.mypage.upload.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.databinding.ItemMypageUploadBinding
import com.runnect.runnect.util.CourseUploadedDiffUtilItemCallback

class MyUploadAdapter(context: Context) :
    ListAdapter<UserUploadCourseDTO, MyUploadViewHolder>(
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
    fun onBind(data: UserUploadCourseDTO) {
        with(binding) {
            Glide.with(itemView).load(data.img).thumbnail(0.3f)
                .format(DecodeFormat.PREFER_RGB_565).into(ivMyPageUploadCourse)
            tvMyPageUploadCourseTitle.text = data.title
            tvMyPageUploadCourseLocation.text = data.departure
        }
    }
}