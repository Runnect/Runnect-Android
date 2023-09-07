package com.runnect.runnect.data.dto


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationData(
    val buildingName: String,
    val fullAddress: String,
) : Parcelable