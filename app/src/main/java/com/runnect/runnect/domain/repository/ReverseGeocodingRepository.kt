package com.runnect.runnect.domain.repository

import com.runnect.runnect.data.dto.LocationData

interface ReverseGeocodingRepository {
    suspend fun getLocationInfoUsingLatLng(lat: Double, lon: Double): LocationData
}