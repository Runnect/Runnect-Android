package com.runnect.runnect.data.model


import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyDrawToRunData(
    val courseId: Int?,
    val publicCourseId: Int?,
    val touchList: ArrayList<LatLng>,
    val startLatLng: LatLng,
    val distance: Float,
    val departure: String,
    val image: String,
) : Parcelable