package com.runnect.runnect.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("app:myDrawDetailImage")
fun loadMyDrawDetailImage(view: ImageView, imageUrl: String) {
    Glide
        .with(view.context)
        .load(imageUrl)
        .circleCrop()
        .into(view)
}

@BindingAdapter("app:storageImage")
fun loadStorageImage(view: ImageView, imageUrl: String) {
    Glide
        .with(view.context)
        .load(imageUrl)
        .centerCrop()
        .into(view)
}

@BindingAdapter("app:endRunImage")
fun loadEndRunImage(view: ImageView, imageUrl: String) {
    Glide
        .with(view.context)
        .load(imageUrl)
        .centerCrop()
        .into(view)
}