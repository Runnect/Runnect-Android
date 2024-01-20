package com.runnect.runnect.util.custom.deco

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.util.extension.dpToPx
import timber.log.Timber

class DiscoverRecommendItemDecoration(
    context: Context,
    rightSpacing: Int,
    bottomSpacing: Int
) : RecyclerView.ItemDecoration() {
    private val rightSpacingPx = rightSpacing.dpToPx(context)
    private val bottomSpacingPx = bottomSpacing.dpToPx(context)

    override fun getItemOffsets(
        outRect: Rect, // 아이템의 사각형 영역
        view: View, // 아이템 뷰
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = rightSpacingPx
        outRect.bottom = bottomSpacingPx
    }
}
