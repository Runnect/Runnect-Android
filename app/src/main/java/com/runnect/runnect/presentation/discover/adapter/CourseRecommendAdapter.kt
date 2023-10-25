package com.runnect.runnect.presentation.discover.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.RecommendCourseDTO
import com.runnect.runnect.databinding.ItemDiscoverCourseInfoBinding
import com.runnect.runnect.util.custom.RunnectToast
import com.runnect.runnect.util.callback.OnHeartClick
import com.runnect.runnect.util.callback.OnItemClick
import com.runnect.runnect.util.extension.stringOf
import com.runnect.runnect.util.callback.ItemDiffCallback

class CourseRecommendAdapter(
    private var context: Context,
    private var heartClick: OnHeartClick,
    private var itemClick: OnItemClick,
    private var isVisitorMode: Boolean
) : ListAdapter<RecommendCourseDTO, CourseRecommendAdapter.CourseInfoViewHolder>(diffUtil) {
    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseInfoViewHolder {
        return CourseInfoViewHolder(
            ItemDiscoverCourseInfoBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CourseInfoViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class CourseInfoViewHolder(private val binding: ItemDiscoverCourseInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: RecommendCourseDTO) {
            with(binding) {
                Glide.with(itemView)
                    .load(data.image)
                    .thumbnail(0.3f)
                    .format(DecodeFormat.PREFER_RGB_565).into(ivItemDiscoverCourseInfoMap)

                tvItemDiscoverCourseInfoTitle.text = data.title
                tvItemDiscoverCourseInfoLocation.text = data.departure
                ivItemDiscoverCourseInfoScrap.isSelected = data.scrap

                ivItemDiscoverCourseInfoScrap.setOnClickListener {
                    if (isVisitorMode) {
                        RunnectToast.createToast(
                            context = context,
                            message = context.stringOf(R.string.visitor_mode_require_login_desc)
                        ).show()
                    } else {
                        it.isSelected = !it.isSelected
                        heartClick.scrapCourse(id = data.id, scrapTF = it.isSelected)
                    }
                }

                root.setOnClickListener {
                    itemClick.selectItem(publicCourseId = data.id)
                }
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<RecommendCourseDTO>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
