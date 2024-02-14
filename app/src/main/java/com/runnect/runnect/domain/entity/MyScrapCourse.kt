package com.runnect.runnect.domain.entity


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyScrapCourse(
    val courseId: Int,
    val id: Int,
    val publicCourseId: Int,
    val image: String?,
    val city: String,
    val region: String,
    val title: String
) : Parcelable