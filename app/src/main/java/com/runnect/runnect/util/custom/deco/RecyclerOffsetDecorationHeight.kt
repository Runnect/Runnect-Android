package com.runnect.runnect.util.custom.deco

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.runnect.runnect.util.extension.dpToPx

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
