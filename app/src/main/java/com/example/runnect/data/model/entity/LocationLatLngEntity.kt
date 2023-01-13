package com.example.runnect.data.model.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationLatLngEntity(
    val latitude: Float,
    val longitude: Float
): Parcelable

