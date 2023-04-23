package com.runnect.runnect.presentation.discover.load.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.runnect.runnect.data.dto.CourseLoadInfoDTO
import com.runnect.runnect.databinding.ItemDiscoverLoadSelectBinding
import com.runnect.runnect.util.DiffUtilItemCallback
import com.runnect.runnect.util.callback.OnRecommendCourseClick
import timber.log.Timber

class DiscoverLoadAdapter(context: Context, listener: OnRecommendCourseClick) :
    ListAdapter<CourseLoadInfoDTO, DiscoverLoadAdapter.DiscoverLoadViewHolder>(DiffUtilItemCallback()) {
    private val inflater by lazy { LayoutInflater.from(context) }
    private val mCallback = listener
    private var beforeSelected: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverLoadViewHolder {
        return DiscoverLoadViewHolder(
            ItemDiscoverLoadSelectBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DiscoverLoadViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class DiscoverLoadViewHolder(private val binding: ItemDiscoverLoadSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: CourseLoadInfoDTO) {
            binding.ivItemDiscoverLoadSelectMap.load(data.img)
            binding.ivItemDiscoverLoadSelectBackground.isSelected = false
            binding.tvItemDiscoverLoadSelectLocation.text = data.departure
            binding.ivItemDiscoverLoadSelectBackground.setOnClickListener {
                Timber.d("1. Adapter에서 Activity에 정의된 콜백함수 호출")
                //오직 하나의 코스만 선택되도록 함
                if (it.isSelected) {
                    mCallback.selectCourse(0,"","","")
                    it.isSelected = false
                    beforeSelected = null
                } else if (!it.isSelected) {
                    if(beforeSelected != null){
                        beforeSelected!!.isSelected = false
                        Timber.d("beforeSelected != null -> $beforeSelected")
                    }
                    beforeSelected = it
                    Timber.d("beforeSelected -> $beforeSelected")
                    mCallback.selectCourse(data.id, data.img, data.departure, data.distance)
                    it.isSelected = true
                }
            }
        }

    }

}