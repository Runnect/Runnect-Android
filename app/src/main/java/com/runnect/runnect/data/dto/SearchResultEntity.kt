package com.runnect.runnect.data.dto

import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResultEntity(
    val fullAddress: String,
    val name: String,
    val locationLatLng: LatLng?,
) : Parcelable
