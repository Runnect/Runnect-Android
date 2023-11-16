package com.runnect.runnect.util.custom.dialog

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommonDialogText(
    val description: String,
    val negativeButtonText: String,
    val positiveButtonText: String
): Parcelable
