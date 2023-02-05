package com.runnect.runnect.data.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailToRunData(
    val departure: String,
    val distance: Int,
    val path: List<List<Double>>,
    val image: String,
) : Parcelable