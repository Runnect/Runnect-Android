package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.LocationData

interface ReverseGeocodingRepository {
    suspend fun getLocationUsingLatLng(lat: Double, lon: Double): LocationData
}