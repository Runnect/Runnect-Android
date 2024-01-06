package com.runnect.runnect.util.custom.deco

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.util.extension.dpToPx

class DiscoverMarathonItemDecoration(
    context: Context,
    spaceSize: Int,
    private val itemCount: Int
) : RecyclerView.ItemDecoration() {
    private val spaceSizePx = spaceSize.dpToPx(context)

    override fun getItemOffsets(
        outRect: Rect, // 아이템의 사각형 영역
        view: View, // 아이템 뷰
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // 마지막 아이템 제외하고는 오른쪽 마진 추가
        if (!isLastItem(view, parent)) {
            outRect.right = spaceSizePx
        }
    }

    private fun isLastItem(view: View, parent: RecyclerView): Boolean {
        val currentItemPosition = parent.getChildAdapterPosition(view)
        return currentItemPosition == itemCount - 1
    }
}