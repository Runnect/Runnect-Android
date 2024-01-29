package com.runnect.runnect.presentation.discover.pick.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.runnect.runnect.databinding.ItemDiscoverPickBinding
import com.runnect.runnect.domain.entity.DiscoverUploadCourse
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.callback.listener.OnCourseUploadItemClick
import timber.log.Timber

class DiscoverPickAdapter(
    private val onCourseUploadItemClick: OnCourseUploadItemClick
) : ListAdapter<DiscoverUploadCourse, DiscoverPickAdapter.DiscoverPickViewHolder>(diffUtil) {
    private var beforePicked: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverPickViewHolder {
        return DiscoverPickViewHolder(
            ItemDiscoverPickBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DiscoverPickViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    fun clearSelection() {
        if (beforePicked != null) {
            beforePicked!!.isSelected = false
            beforePicked = null
            onCourseUploadItemClick.selectCourse(0, "", "", "")
        }
    }

    inner class DiscoverPickViewHolder(private val binding: ItemDiscoverPickBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: DiscoverUploadCourse) {
            binding.ivItemDiscoverPickMap.load(data.img)
            binding.ivItemDiscoverPickBackground.isSelected = false
            binding.tvItemDiscoverPickLocation.text = data.departure

            binding.ivItemDiscoverPickBackground.setOnClickListener {
                Timber.d("1. Adapter에서 Activity에 정의된 콜백함수 호출")
                //오직 하나의 코스만 선택되도록 함
                if (it.isSelected) {
                    onCourseUploadItemClick.selectCourse(0, "", "", "")
                    it.isSelected = false
                    beforePicked = null
                } else if (!it.isSelected) {
                    if (beforePicked != null) {
                        beforePicked!!.isSelected = false
                    }
                    beforePicked = it
                    onCourseUploadItemClick.selectCourse(data.id, data.img, data.departure, data.distance)
                    it.isSelected = true
                }
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverUploadCourse>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
