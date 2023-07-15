package com.runnect.runnect.data.model


import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BannerData(
    val imageUrl: String,
    val linkUrl: String,
) : Parcelable