package com.runnect.runnect.presentation.storage.adapter

import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView


fun setSelectionTracker(id: String, recyclerView: RecyclerView): SelectionTracker<Long> {
    return SelectionTracker.Builder(
        id,
        recyclerView,
        StableIdKeyProvider(recyclerView),
        StorageMyDrawLookUp(recyclerView),
        StorageStrategy.createLongStorage()
    ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
        .build()
}