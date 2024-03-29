package com.runnect.runnect.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DiscoverUploadCourse(
    val id: Int,
    val imageUrl: String,
    val departure: String,
    val distance: String
) : Parcelable
