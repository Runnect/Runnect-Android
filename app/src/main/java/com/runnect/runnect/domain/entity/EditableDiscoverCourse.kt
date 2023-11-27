package com.runnect.runnect.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditableDiscoverCourse(
    val title: String,
    val scrap: Boolean
): Parcelable
