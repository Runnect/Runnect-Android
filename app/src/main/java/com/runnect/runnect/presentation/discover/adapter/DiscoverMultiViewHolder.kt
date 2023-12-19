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
        private var marathonCourses = listOf<MarathonCourse>()
        private val marathonAdapter by lazy {
            DiscoverMarathonAdapter(
                onHeartButtonClick, onCourseItemClick, handleVisitorMode
            )
        }

        fun bind(marathonCourses: List<MarathonCourse>) {
            this.marathonCourses = marathonCourses
            initMarathonRecyclerView()
        }

        private fun initMarathonRecyclerView() {
            binding.rvDiscoverMarathon.apply {
                setHasFixedSize(true)
                adapter = marathonAdapter.apply {
                    submitList(marathonCourses)
                }
                addItemDecoration(
                    DiscoverMarathonItemDecoration(
                        context = context,
                        spaceSize = 10,
                        itemCount = marathonCourses.size
                    )
                )
            }
        }

        fun updateMarathonCourseItem(
            publicCourseId: Int,
            updatedCourse: EditableDiscoverCourse
        ) {
            marathonCourses.forEachIndexed { index, course ->
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
        private val onNextPageLoad: () -> Unit,
        onHeartButtonClick: (Int, Boolean) -> Unit,
        onCourseItemClick: (Int) -> Unit,
        handleVisitorMode: () -> Unit,
    ) : DiscoverMultiViewHolder(binding) {
        private var recommendCourses = listOf<RecommendCourse>()
        private val recommendAdapter by lazy {
            DiscoverRecommendAdapter(
                onHeartButtonClick,
                onCourseItemClick,
                handleVisitorMode
            )
        }

        fun bind(recommendCourses: List<RecommendCourse>) {
            this.recommendCourses = recommendCourses
            initRecommendRecyclerView()
            initScrollListener()
        }

        private fun initRecommendRecyclerView() {
            binding.rvDiscoverRecommend.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 2)
                adapter = recommendAdapter.apply {
                    submitList(recommendCourses)
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

        private fun initScrollListener() {
            binding.rvDiscoverRecommend.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()

                    if (lastItemPosition + PRE_PATCH_DISTANCE >= layoutManager.itemCount) {
                        Timber.e("마지막 아이템 위치: ${lastItemPosition}")
                        Timber.e("현재 아이템 개수: ${layoutManager.itemCount}")
                        Timber.e("스크롤이 끝에 도달했어요!")

                        onNextPageLoad.invoke()
                    }
                }
            })
        }

        fun updateRecommendCourses(courses: List<RecommendCourse>) {
            Timber.e("추천 코스를 갱신했어요!")
            recommendAdapter.submitList(courses)
        }

        fun updateRecommendCourseItem(
            publicCourseId: Int,
            updatedCourse: EditableDiscoverCourse
        ) {
            recommendCourses.forEachIndexed { index, course ->
                if (course.id == publicCourseId) {
                    course.title = updatedCourse.title
                    course.scrap = updatedCourse.scrap
                    recommendAdapter.notifyItemChanged(index)
                    return@forEachIndexed
                }
            }
        }
    }

    companion object {
        private const val PRE_PATCH_DISTANCE = 2
    }
}
