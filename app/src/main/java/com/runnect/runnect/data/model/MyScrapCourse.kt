package com.runnect.runnect.data.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyScrapCourse(
    val publicId: Int?,
    val privateCourseId: Int?,
    val image: String?,
    val city: String,
    val region: String,
    val title : String
) : Parcelable