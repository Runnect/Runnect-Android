package com.runnect.runnect.data.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RunToEndRunData(
    val courseId : Int,
    val publicCourseId : Int? = null,
    val totalDistance: Double?,
    val captureUri: String?,
    val departure: String?,
    val timerHour : String?,
    val timerMinute: String?,
    val timerSecond: String?,
    val timeTotal: Int?,
    val dataFrom: String
) : Parcelable
