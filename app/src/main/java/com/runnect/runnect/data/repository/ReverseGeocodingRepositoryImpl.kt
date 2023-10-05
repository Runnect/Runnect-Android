package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.LocationData
import com.runnect.runnect.data.source.remote.RemoteReverseGeocodingDataSource
import com.runnect.runnect.domain.ReverseGeocodingRepository
import javax.inject.Inject

class ReverseGeocodingRepositoryImpl @Inject constructor(private val reverseGeocodingDataSource: RemoteReverseGeocodingDataSource) :
    ReverseGeocodingRepository {
    override suspend fun getLocationInfoUsingLatLng(
        lat: Double,
        lon: Double
    ): LocationData {
        val response =
            reverseGeocodingDataSource.getLocationInfoUsingLatLng(lat = lat, lon = lon).body()
        return LocationData(
            buildingName = response?.addressInfo?.buildingName ?: "buildingName fail",
            fullAddress = response?.addressInfo?.fullAddress ?: "fullAddress fail"
        )
    }
}