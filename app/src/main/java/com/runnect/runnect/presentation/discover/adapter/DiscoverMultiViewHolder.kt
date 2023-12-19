package com.runnect.runnect.presentation.discover.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverMultiviewMarathonBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendBinding
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
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

    class RecommendCourseViewHolder(
        private val binding: ItemDiscoverMultiviewRecommendBinding,
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
            initRecommendRecyclerView(courses)
        }

        private fun initRecommendRecyclerView(courses: List<RecommendCourse>) {
            binding.rvDiscoverRecommend.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 2)
                adapter = recommendAdapter.apply {
                    submitList(courses)
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

        fun updateRecommendCourses(courses: List<RecommendCourse>) {
            Timber.e("추천 코스를 갱신했어요!")
            recommendAdapter.submitList(courses)
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
