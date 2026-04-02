package com.runnect.runnect.util.custom.deco

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.runnect.runnect.util.extension.dpToPx

/** Set recyclerview item spacing for grid layout manager */
class GridSpacingItemDecoration(
    context: Context,
    private val spanCount: Int,
    horizontalSpaceSize: Int,
    topSpaceSize: Int
) : ItemDecoration() {
    private val horizontalSpacing = horizontalSpaceSize.dpToPx(context)
    private val topSpacing = topSpaceSize.dpToPx(context)
//    private val includeEdge = false

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val columnIdx = position % spanCount

        // column * ((1f / spanCount) * spacing)
        outRect.left = columnIdx * horizontalSpacing / spanCount

        // spacing - (column + 1) * ((1f /    spanCount) * spacing)
        outRect.right = horizontalSpacing - (columnIdx + 1) * horizontalSpacing / spanCount

        // 두번째 행부터 적용되는 top spacing
        if (position >= spanCount) {
            outRect.top = topSpacing
        }

//        // 모서리부분 포함
//        if (includeEdge) {
//            // spacing - column * ((1f / spanCount) * spacing)
//            outRect.left = horizontalSpacingSize - columnIdx * horizontalSpacingSize / spanCount
//
//            // (column + 1) * ((1f / spanCount) * spacing)
//            outRect.right = (columnIdx + 1) * horizontalSpacingSize / spanCount
//
//            // top edge
//            if (position < spanCount) {
//                outRect.top = topSpacingSize
//            }
//
//            // item bottom 두 번째 행이상부터 적용되는 bottom space
//            outRect.bottom = topSpacingSize
//        }
//        // 모서리부분 미포함
//        else {
//
//        }
    }
}
