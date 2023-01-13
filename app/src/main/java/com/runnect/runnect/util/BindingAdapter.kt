package com.runnect.runnect.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

object BindingAdapter {
    @BindingAdapter("imgRes")
    @JvmStatic
    fun imgRes(imageView: ImageView, resId: Int) {
        imageView.load(resId)
    }
}