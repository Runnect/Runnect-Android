package com.runnect.runnect.util.custom.deco

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.util.extension.dpToPx

class DiscoverRecommendItemDecoration(
    context: Context,
    rightSpacing: Int,
    bottomSpacing: Int,
    private val spanCount: Int
) : RecyclerView.ItemDecoration() {
    private val rightSpacingPx = rightSpacing.dpToPx(context)
    private val bottomSpacingPx = bottomSpacing.dpToPx(context)

    override fun getItemOffsets(
        outRect: Rect, // 아이템의 사각형 영역
        view: View, // 아이템 뷰
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // 마지막 열 제외하고는 오른쪽 마진 추가
        if (!isLastColumn(view, parent)) {
            outRect.right = rightSpacingPx
        }

        // 모든 아이템에 대해서 아래쪽 마진 추가
        outRect.bottom = bottomSpacingPx
    }

    private fun isLastColumn(view: View, parent: RecyclerView): Boolean {
        val currentItemPosition = parent.getChildAdapterPosition(view)
        return currentItemPosition % spanCount == spanCount - 1
    }
}
