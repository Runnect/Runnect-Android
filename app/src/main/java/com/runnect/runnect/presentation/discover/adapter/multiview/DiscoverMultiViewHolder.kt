package com.runnect.runnect.presentation.discover.adapter.multiview

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverMultiviewMarathonBinding
import com.runnect.runnect.databinding.ItemDiscoverMultiviewRecommendBinding
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import com.runnect.runnect.presentation.discover.adapter.DiscoverMarathonAdapter
import com.runnect.runnect.presentation.discover.adapter.DiscoverRecommendAdapter
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import com.runnect.runnect.util.custom.deco.DiscoverMarathonItemDecoration
import com.runnect.runnect.util.custom.deco.DiscoverRecommendItemDecoration
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration
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
            targetIndex: Int,
            updatedCourse: EditableDiscoverCourse
        ) {
            marathonAdapter.currentList[targetIndex].apply {
                title = updatedCourse.title
                scrap = updatedCourse.scrap
            }
            marathonAdapter.notifyItemChanged(targetIndex)
        }

        fun updateMarathonCourseScrap(targetIndex: Int, scrap: Boolean) {
            marathonAdapter.currentList[targetIndex].apply {
                this.scrap = scrap
            }
            marathonAdapter.notifyItemChanged(targetIndex)
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
            Timber.d("추천 코스 리스트 크기: ${courses.size}")
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
                        bottomSpacing = 20
                    )
                )
            }
        }

        fun updateRecommendCourseItem(
            targetIndex: Int,
            updatedCourse: EditableDiscoverCourse
        ) {
            recommendAdapter.currentList[targetIndex].apply {
                title = updatedCourse.title
                scrap = updatedCourse.scrap
            }
            recommendAdapter.notifyItemChanged(targetIndex)
        }

        fun updateRecommendCourseScrap(targetIndex: Int, scrap: Boolean) {
            recommendAdapter.currentList[targetIndex].apply {
                this.scrap = scrap
            }
            recommendAdapter.notifyItemChanged(targetIndex)
        }
    }
}
