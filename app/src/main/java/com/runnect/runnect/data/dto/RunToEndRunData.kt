package com.runnect.runnect.data.dto


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RunToEndRunData(
    val courseId: Int,
    val publicCourseId: Int? = null,
    val totalDistance: Double?,
    val captureUri: String?,
    val departure: String?,
    val timerHour: Int?,
    val timerMinute: Int?,
    val timerSecond: Int?,
    val dataFrom: String
) : Parcelable
