package com.runnect.runnect.util.binding

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import com.bumptech.glide.Glide

object BindingAdapter {
    @BindingAdapter("setLocalImageByResourceId")
    @JvmStatic
    fun setLocalImageByResourceId(imageView: ImageView, resId: Int) {
        imageView.load(resId)
    }

    @BindingAdapter("setLocalImageByUri")
    @JvmStatic
    fun setLocalImageByUri(imageView: ImageView, uri: String) {
        imageView.load(uri)
    }

    @BindingAdapter("setRemoteImageByUrl")
    @JvmStatic
    fun setRemoteImageByUrl(view: ImageView, imageUrl: String) {
        Glide.with(view.context)
            .load(imageUrl)
            .centerCrop()
            .into(view)
    }
}
