package com.runnect.runnect.presentation.storage.adapter

import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView


fun setSelectionTracker(id: String, recyclerView: RecyclerView): SelectionTracker<Long> {
    val tracker = SelectionTracker.Builder(
        id,
        recyclerView,
        StableIdKeyProvider(recyclerView), // ItemKeyProvider는 리사이클러뷰의 아이템을 눌렀을 때, key를 가져오기 위해서 사용하는 클래스이다.
        StorageMyDrawLookUp(recyclerView),
        StorageStrategy.createLongStorage() //ItemKey를 저장시켜놓는 클래스입니다. ItemKeyProvider에 따라, Long, String, Parcelable 형태로 Stroage를 만들어줍니다.
    ).withSelectionPredicate(object : SelectionTracker.SelectionPredicate<Long>() {
        override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean { //무슨 역할?
            return true
        }

        override fun canSetStateAtPosition( //무슨 역할?
            position: Int, nextState: Boolean,
        ): Boolean {
            return true
        }

        override fun canSelectMultiple(): Boolean { //무슨 역할?
            return true
        }
    }).build()
//        .withSelectionPredicate(SelectionPredicates.<Long>createSelectAnything())
//        .build(); 노션 링크에서는 이렇게만 돼있었는데 커스텀해준듯.
    return tracker
}