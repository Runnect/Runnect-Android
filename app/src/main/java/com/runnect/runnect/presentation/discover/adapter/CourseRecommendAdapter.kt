package com.runnect.runnect.presentation.discover.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.runnect.runnect.data.dto.RecommendCourseDTO
import com.runnect.runnect.databinding.ItemDiscoverCourseInfoBinding
import com.runnect.runnect.util.CustomToast
import com.runnect.runnect.util.RecommendCourseDiffUtilItemCallback
import com.runnect.runnect.util.callback.OnHeartClick
import com.runnect.runnect.util.callback.OnItemClick

class CourseRecommendAdapter(
    private var context: Context,
    private var heartClick: OnHeartClick,
    private var itemClick: OnItemClick,
    private var isVisitorMode: Boolean
) :
    ListAdapter<RecommendCourseDTO, CourseRecommendAdapter.CourseInfoViewHolder>(
        RecommendCourseDiffUtilItemCallback()
    ) {

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
                Glide.with(itemView).load(data.image).thumbnail(0.3f)
                    .format(DecodeFormat.PREFER_RGB_565).into(ivItemDiscoverCourseInfoMap)
                tvItemDiscoverCourseInfoTitle.text = data.title
                tvItemDiscoverCourseInfoLocation.text = data.departure
                ivItemDiscoverCourseInfoScrap.isSelected = data.scrap
                ivItemDiscoverCourseInfoScrap.setOnClickListener {
                    if (isVisitorMode) {
                        CustomToast.createToast(context = context, message = REQUIRE_LOGIN)
                            .show()
                    } else {
                        it.isSelected = !it.isSelected
                        heartClick.scrapCourse(data.id, it.isSelected)
                    }
                }
                root.setOnClickListener {
                    itemClick.selectItem(data.id)
                }
            }
        }
    }

    companion object {
        const val REQUIRE_LOGIN = "러넥트에 가입하면 코스를 스크랩할 수 있어요"
    }
}

