package com.runnect.runnect.data.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailToRunData(
    val courseId : Int?,
    val publicCourseId : Int?,
    val departure: String,
    val distance: Float,
    val path: List<List<Double>>,
    val image: String,
) : Parcelable