package com.runnect.runnect.data.model


import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountToRunData(
    val courseId: Int?,
    val publicCourseId: Int?,
    val departure: String,
    val distance: Float,
    val touchList: ArrayList<LatLng>,
    val startLatLng: LatLng,
    val image: String,
    val dataFrom : String
) : Parcelable