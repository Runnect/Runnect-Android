package com.runnect.runnect.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class RecyclerOffsetDecorationHeight(private val context:Context, private val divHeight: Int) : ItemDecoration() {
    val offset = divHeight.dpToPx(context)
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
            outRect.bottom = offset
        }
    }
}
// dp -> pixel 단위로 변경

private fun Int.dpToPx(context: Context): Int {
    return context.resources.displayMetrics.density.let { density ->
        (this * density).toInt()
    }

}