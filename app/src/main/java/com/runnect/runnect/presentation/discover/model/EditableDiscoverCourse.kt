package com.runnect.runnect.presentation.discover.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditableDiscoverCourse(
    val title: String,
    val scrap: Boolean
): Parcelable
