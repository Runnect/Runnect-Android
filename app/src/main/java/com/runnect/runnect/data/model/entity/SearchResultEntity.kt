package com.runnect.runnect.data.model.entity

import android.os.Parcelable
import com.runnect.runnect.data.model.entity.LocationLatLngEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResultEntity(
    val fullAdress: String,
    val name: String,
    val locationLatLng: LocationLatLngEntity
): Parcelable
