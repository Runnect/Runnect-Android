package com.runnect.runnect.data.dto


import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseData(
    val courseId: Int?,
    val publicCourseId: Int?,
    val touchList: ArrayList<LatLng>,
    val startLatLng: LatLng,
    val departure: String?,
    val distance: Float?,
    val image: String,
    val dataFrom: String
) : Parcelable