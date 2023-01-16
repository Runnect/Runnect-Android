package com.runnect.runnect.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/* set spacing for grid view */
class GridSpacingItemDecoration(
    private val context: Context,
    private val spanCount: Int,
    private val s: Int,
    private val top: Int
) :
    ItemDecoration() {
    private val spacing = s.dpToPx(context)
    private val spacing_top = top.dpToPx(context)
    private val includeEdge = false
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item phases_position
        val column = position % spanCount // item column
        //모서리부분 포함
        if (includeEdge) {
            outRect.left =
                spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right =
                (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)
            if (position < spanCount) { // top edge
                outRect.top = spacing_top
            }
            outRect.bottom = spacing_top // item bottom 두 번째 행이상부터 적용되는 bottom space
        }
        //모서리부분 미 포함
        else {
            outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right =
                spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing_top // item top 두 번째 행이상부터 적용되는 top space
            }
        }
    }
}
private fun Int.dpToPx(context: Context): Int {
    return context.resources.displayMetrics.density.let { density ->
        (this * density).toInt()
    }

}



