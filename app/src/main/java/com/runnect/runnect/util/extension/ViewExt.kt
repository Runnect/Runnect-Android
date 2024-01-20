package com.runnect.runnect.util.extension

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.Px
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

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

fun <T : ViewDataBinding> ViewGroup.getViewDataBinding(
    @LayoutRes layoutRes: Int
): T = DataBindingUtil.inflate(
    LayoutInflater.from(this.context),
    layoutRes,
    this,
    false
)