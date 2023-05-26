package com.runnect.runnect.data.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyDrawCourse(
    val courseId: Int?,
    val image: String?,
    val city: String,
    val region: String,
) : Parcelable