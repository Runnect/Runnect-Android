package com.runnect.runnect.data.dto


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimerData(
    val hour: Int?,
    val minute: Int?,
    val second: Int?,
) : Parcelable