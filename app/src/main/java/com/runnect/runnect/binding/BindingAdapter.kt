package com.runnect.runnect.binding

import android.annotation.SuppressLint
import android.graphics.Color
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.runnect.runnect.R

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


@BindingAdapter("app:image_select")
fun ImageView.loadSelect(selected: Boolean) {
    if (selected) {
        setBackgroundResource(R.drawable.select)
    } else {
        setBackgroundColor(Color.parseColor("#FFFFFF"))
    }
}

@BindingAdapter("app:image_check")
fun ImageView.loadCheck(selected: Boolean) {
    if (selected) {
        setBackgroundResource(R.drawable.ic_select)
    } else {
        setBackgroundResource(R.drawable.ic_not_select)
    }
}