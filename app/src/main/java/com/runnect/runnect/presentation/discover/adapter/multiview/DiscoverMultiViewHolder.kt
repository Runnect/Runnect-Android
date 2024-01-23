package com.runnect.runnect.presentation.discover.adapter.multiview

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.R
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
import com.runnect.runnect.util.extension.colorOf
import com.runnect.runnect.util.extension.fontOf
import timber.log.Timber

sealed class DiscoverMultiViewHolder(binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    class MarathonCourseViewHolder(
        private val binding: ItemDiscoverMultiviewMarathonBinding,
        onHeartButtonClick: (Int, Boolean) -> Unit,
        onCourseItemClick: (Int) -> Unit,
        handleVisitorMode: () -> Unit
    ) : DiscoverMultiViewHolder(binding) {
        val marathonAdapter by lazy {
            DiscoverMarathonAdapter(
                onHeartButtonClick, onCourseItemClick, handleVisitorMode
            )
        }

        fun bind(courses: List<MarathonCourse>) {
            initMarathonRecyclerView(courses)
        }

        private fun initMarathonRecyclerView(courses: List<MarathonCourse>) {
            binding.rvDiscoverMarathon.apply {
                adapter = marathonAdapter.apply {
                    submitList(courses)
                }
                setHasFixedSize(true)
                addItemDecorationOnlyOnce(this, courses.size)
            }
        }

        private fun addItemDecorationOnlyOnce(recyclerView: RecyclerView, itemCount: Int) {
            with(recyclerView) {
                if (itemDecorationCount > 0) {
                    removeItemDecorationAt(0)
                }
                addItemDecoration(
                    DiscoverMarathonItemDecoration(
                        context = context,
                        spaceSize = 10,
                        itemCount = itemCount
                    )
                )
            }
        }
    }

    class RecommendCourseViewHolder(
        private val binding: ItemDiscoverMultiviewRecommendBinding,
        onHeartButtonClick: (Int, Boolean) -> Unit,
        onCourseItemClick: (Int) -> Unit,
        handleVisitorMode: () -> Unit,
        private val onSortButtonClick: (String) -> Unit
    ) : DiscoverMultiViewHolder(binding) {
        val recommendAdapter by lazy {
            DiscoverRecommendAdapter(
                onHeartButtonClick,
                onCourseItemClick,
                handleVisitorMode
            )
        }

        fun bind(courses: List<RecommendCourse>) {
            Timber.d("추천 코스 리스트 크기: ${courses.size}")
            initRecommendRecyclerView(courses)
            initSortButtonClickListener()
        }

        private fun initRecommendRecyclerView(courses: List<RecommendCourse>) {
            binding.rvDiscoverRecommend.apply {
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
        private fun initSortButtonClickListener() {
            initSortByDateClickListener()
            initSortByScrapClickListener()
        }

        private fun initSortByDateClickListener() {
            binding.tvDiscoverRecommendSortByDate.setOnClickListener {
                val context = it.context ?: return@setOnClickListener

                activateTextStyle(view = it as TextView, context = context)
                deactivateOtherTextStyle(
                    view = binding.tvDiscoverRecommendSortByScrap,
                    context = context
                )

                onSortButtonClick.invoke(SORT_BY_DATE)
            }
        }

        private fun initSortByScrapClickListener() {
            binding.tvDiscoverRecommendSortByScrap.setOnClickListener {
                val context = it.context ?: return@setOnClickListener

                activateTextStyle(view = it as TextView, context = context)
                deactivateOtherTextStyle(
                    view = binding.tvDiscoverRecommendSortByDate,
                    context = context
                )

                onSortButtonClick.invoke(SORT_BY_SCRAP)
            }
        }

        private fun activateTextStyle(view: TextView, context: Context) {
            view.setTextColor(context.colorOf(R.color.M1))
            view.typeface = context.fontOf(R.font.pretendard_semibold, Typeface.NORMAL)
        }

        private fun deactivateOtherTextStyle(view: TextView, context: Context) {
            view.setTextColor(context.colorOf(R.color.G2))
            view.typeface = context.fontOf(R.font.pretendard_regular, Typeface.NORMAL)
        }

        companion object {
            private const val SORT_BY_DATE = "date"
            private const val SORT_BY_SCRAP = "scrap"
        }
    }
}
