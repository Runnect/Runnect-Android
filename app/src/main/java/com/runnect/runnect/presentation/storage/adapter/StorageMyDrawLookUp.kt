package com.runnect.runnect.presentation.storage.adapter

import android.view.MotionEvent
import androidx.annotation.Nullable
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView


class StorageMyDrawLookUp(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(motionEvent: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(motionEvent.x, motionEvent.y)
        if (view != null) {
            val viewHolder  =
                recyclerView.getChildViewHolder(view) as StorageMyDrawAdapter.ItemViewHolder
            return viewHolder.getItemDetails()
        }
        return null
    }
}