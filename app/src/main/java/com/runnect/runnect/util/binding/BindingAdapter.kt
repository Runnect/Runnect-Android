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

@BindingAdapter("setStampImageByResourceId")
fun ImageView.setStampImageByResourceId(stampId: String?) {
    val resNameParam = "mypage_img_stamp_"
    val resType = "drawable"
    val packageName = context.packageName

    var resName = ""
    resName = if (stampId == "CSPR0") {
        "${resNameParam}basic"
    } else {
        "${resNameParam}$stampId"
    }
    val resId = context.resources.getIdentifier(resName, resType, packageName)
    setImageResource(resId)
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

@BindingAdapter("setDiscoverItemImage")
fun ImageView.setDiscoverItemImage(imageUrl: String?) {
    Glide.with(context)
        .load(imageUrl)
        .thumbnail(0.3f)
        .format(DecodeFormat.PREFER_RGB_565)
        .into(this)
}
