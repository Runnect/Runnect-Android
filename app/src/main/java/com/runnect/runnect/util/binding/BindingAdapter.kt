package com.runnect.runnect.util.binding

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat

@BindingAdapter("setLocalImageByResourceId")
fun ImageView.setLocalImageByResourceId(resId: Int) {
    load(resId)
}

@BindingAdapter("setImageUrl")
fun ImageView.setImageUrl(url: String?) {
    if (url == null) return
    load(url)
}

@BindingAdapter("isValidTitle", "isValidDescription")
fun AppCompatButton.updateEditFinishButtonBackground(
    isValidTitle: Boolean,
    isValidDescription: Boolean
) {
    isEnabled = isValidTitle && isValidDescription
}

@BindingAdapter("setDiscoverPromotionImage")
fun ImageView.setDiscoverPromotionImage(imageUrl: String) {
    Glide.with(context)
        .load(imageUrl)
        .thumbnail(0.3f)
        .format(DecodeFormat.PREFER_RGB_565)
        .into(this)
}
