package com.runnect.runnect.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ItemOffsetDecoration(context: Context, widthOffset: Int, heightOffset: Int) :
    RecyclerView.ItemDecoration() {

    private val width = widthOffset.dpToPx(context)
    private val height = heightOffset.dpToPx(context)

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        // spanIndex = 0 -> 왼쪽
        // spanIndex = 1 -> 오른쪽
        val lp = view.layoutParams as GridLayoutManager.LayoutParams
        when (lp.spanIndex) {
            0 -> {
                outRect.right = width
                outRect.bottom = height
            }
            1 -> {
                outRect.left = width
                outRect.bottom = height
            }
        }
    }

    // dp -> pixel 단위로 변경

    private fun Int.dpToPx(context: Context): Int {
        return context.resources.displayMetrics.density.let { density ->
            (this * density).toInt()
        }

    }
}