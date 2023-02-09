package com.runnect.runnect.data.model


import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.data.model.entity.LocationLatLngEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DrawToRunData(
    val courseId : Int,
    val publicCourseId : Int? = null,
    val touchList: ArrayList<LatLng>,
    val startLatLng: LocationLatLngEntity,
    val totalDistance: Float?,
    val departure: String,
    val captureUri: String,
) : Parcelable