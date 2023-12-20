package com.runnect.runnect.presentation.discover.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverMultiviewMarathonBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendCourseBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendHeaderBinding
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.*
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import com.runnect.runnect.util.custom.deco.DiscoverMarathonItemDecoration
import com.runnect.runnect.util.custom.deco.DiscoverRecommendItemDecoration
import timber.log.Timber

sealed class DiscoverMultiViewHolder(binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    class MarathonCourseViewHolder(
        private val binding: ItemDiscoverMultiviewMarathonBinding,
        onHeartButtonClick: (Int, Boolean) -> Unit,
        onCourseItemClick: (Int) -> Unit,
        handleVisitorMode: () -> Unit
    ) : DiscoverMultiViewHolder(binding) {
        private val marathonAdapter by lazy {
            DiscoverMarathonAdapter(
                onHeartButtonClick, onCourseItemClick, handleVisitorMode
            )
        }

        fun bind(courses: List<MarathonCourse>) {
            initMarathonRecyclerView(courses)
        }

        private fun initMarathonRecyclerView(courses: List<MarathonCourse>) {
            binding.rvDiscoverMarathon.apply {
                setHasFixedSize(true)
                adapter = marathonAdapter.apply {
                    submitList(courses)
                }
                addItemDecoration(
                    DiscoverMarathonItemDecoration(
                        context = context,
                        spaceSize = 10,
                        itemCount = courses.size
                    )
                )
            }
        }

        fun updateMarathonCourseItem(
            publicCourseId: Int,
            updatedCourse: EditableDiscoverCourse
        ) {
            marathonAdapter.currentList.forEachIndexed { index, course ->
                if (course.id == publicCourseId) {
                    course.title = updatedCourse.title
                    course.scrap = updatedCourse.scrap
                    marathonAdapter.notifyItemChanged(index)
                    return@forEachIndexed
                }
            }
        }
    }

    // todo: 정렬 버튼 클릭 리스너 구현
    class RecommendHeaderViewHolder(
       private val binding: ItemDiscoverMultiviewRecommendHeaderBinding
    ) : DiscoverMultiViewHolder(binding) {
        fun bind(headers: List<RecommendHeader>) {
            binding.header = headers.first()
        }
    }

    class RecommendCourseViewHolder(
        private val binding: ItemDiscoverMultiviewRecommendCourseBinding,
        onHeartButtonClick: (Int, Boolean) -> Unit,
        onCourseItemClick: (Int) -> Unit,
        handleVisitorMode: () -> Unit,
    ) : DiscoverMultiViewHolder(binding) {
        private val recommendAdapter by lazy {
            DiscoverRecommendAdapter(
                onHeartButtonClick,
                onCourseItemClick,
                handleVisitorMode
            )
        }

        fun bind(courses: List<RecommendCourse>) {
            Timber.e("추천 코스 리스트 바인딩!!!")
            initRecommendRecyclerView(courses)
        }

        private fun initRecommendRecyclerView(courses: List<RecommendCourse>) {
            binding.rvDiscoverRecommend.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 2)
                adapter = recommendAdapter.apply {
                    submitList(courses)
                }
                addItemDecorationOnlyOnce(this)
            }
        }

        private fun addItemDecorationOnlyOnce(recyclerView: RecyclerView) {
            with(recyclerView) {
                if (itemDecorationCount > 0) {
                    removeItemDecorationAt(0)
                }
                addItemDecoration(
                    DiscoverRecommendItemDecoration(
                        context = context,
                        rightSpacing = 6,
                        bottomSpacing = 20,
                        spanCount = 2
                    )
                )
            }
        }

        fun updateRecommendCourseItem(
            publicCourseId: Int,
            updatedCourse: EditableDiscoverCourse
        ) {
            recommendAdapter.currentList.forEachIndexed { index, course ->
                if (course.id == publicCourseId) {
                    course.title = updatedCourse.title
                    course.scrap = updatedCourse.scrap
                    recommendAdapter.notifyItemChanged(index)
                    return@forEachIndexed
                }
            }
        }
    }
}
