package com.runnect.runnect.data.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RunToEndRunData(
    val totalDistance: Double?,
    val captureUri: String?,
    val departure: String?,
    val timerSec: String?,
    val timerMilli: String?,
) : Parcelable
