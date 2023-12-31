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

    // 리사이클러뷰 아이템 주위의 여백을 설정하는 함수
    override fun getItemOffsets(
        outRect: Rect, // 아이템의 사각형 영역
        view: View, // 아이템 뷰
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // 마지막 아이템에 대해서만 오른쪽 여백 제거
        if (!checkLastItem(view, parent)) {
            outRect.right = spaceSizePx
        }
    }

    private fun checkLastItem(view: View, parent: RecyclerView): Boolean {
        return parent.getChildAdapterPosition(view) == itemCount - 1
    }
}