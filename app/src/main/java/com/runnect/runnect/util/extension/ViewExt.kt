package com.runnect.runnect.util.extension

import android.view.View
import androidx.annotation.Px

inline fun View.setOnSingleClickListener(
    delay: Long = 500L,
    crossinline block: (View) -> Unit
) {
    var previousClickedTime = 0L
    setOnClickListener { view ->
        val clickedTime = System.currentTimeMillis()
        if (clickedTime - previousClickedTime >= delay) {
            block(view)
            previousClickedTime = clickedTime
        }
    }
}

fun View.setPadding(@Px size: Int) {
    setPadding(size, size, size, size)
}