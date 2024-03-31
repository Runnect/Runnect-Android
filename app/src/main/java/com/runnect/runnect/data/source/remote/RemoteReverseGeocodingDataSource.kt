package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.dto.response.ResponseReverseGeocodingDto
import com.runnect.runnect.data.service.ReverseGeocodingService
import javax.inject.Inject

class RemoteReverseGeocodingDataSource @Inject constructor(private val reverseGeocodingService: ReverseGeocodingService) {
    suspend fun getLocationInfoUsingLatLng(
        lat: Double,
        lon: Double
    ): Result<ResponseReverseGeocodingDto> =
        reverseGeocodingService.getLocationUsingLatLng(lat = lat, lon = lon)
}