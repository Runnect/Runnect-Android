package com.runnect.runnect.util.binding

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import coil.load

object BindingAdapter {
    @BindingAdapter("setLocalImageByResourceId")
    @JvmStatic
    fun setLocalImageByResourceId(imageView: ImageView, resId: Int) {
        imageView.load(resId)
    }

    @BindingAdapter("setImageUrl")
    @JvmStatic
    fun setImageUrl(imageView: ImageView, url: String?) {
        if(url == null) return
        imageView.load(url)
    }

    @BindingAdapter("isValidTitle", "isValidDescription")
    @JvmStatic
    fun updateEditFinishButtonBackground(
        button: AppCompatButton,
        isValidTitle: Boolean,
        isValidDescription: Boolean
    ) {
        button.isEnabled = isValidTitle && isValidDescription
    }
}
