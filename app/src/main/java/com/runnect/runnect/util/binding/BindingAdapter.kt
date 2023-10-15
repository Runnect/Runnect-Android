package com.runnect.runnect.util.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.bumptech.glide.Glide

object BindingAdapter {
    @BindingAdapter("imgRes")
    @JvmStatic
    fun imgRes(imageView: ImageView, resId: Int) {
        imageView.load(resId)
    }

    @BindingAdapter("imgUri")
    @JvmStatic
    fun imgUri(imageView: ImageView, uri: String) {
        imageView.load(uri)
    }

    @BindingAdapter("loadStorageImage")
    @JvmStatic
    fun loadStorageImage(view: ImageView, imageUrl: String) {
        Glide.with(view.context)
            .load(imageUrl)
            .centerCrop()
            .into(view)
    }

    @BindingAdapter("loadEndRunImage")
    @JvmStatic
    fun loadEndRunImage(view: ImageView, imageUrl: String) {
        Glide.with(view.context)
            .load(imageUrl)
            .centerCrop()
            .into(view)
    }
}
