package com.runnect.runnect.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("app:rewardImage")
fun loadRewardImage(view: ImageView, imageUrl: String) {
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