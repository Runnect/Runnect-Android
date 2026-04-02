package com.runnect.runnect.domain.repository

import com.runnect.runnect.domain.entity.LocationData
import kotlinx.coroutines.flow.Flow

interface ReverseGeocodingRepository {
    suspend fun getLocationInfoUsingLatLng(lat: Double, lon: Double): Flow<Result<LocationData>>
}