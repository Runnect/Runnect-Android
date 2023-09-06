package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.dto.tmap.geocoding.ResponseReverseGeocodingDto
import com.runnect.runnect.data.service.ReverseGeocodingService
import retrofit2.Response
import javax.inject.Inject

class RemoteReverseGeocodingDataSource @Inject constructor(private val reverseGeocodingService: ReverseGeocodingService) {
    suspend fun getLocationUsingLatLng(
        lat: Double,
        lon: Double
    ): Response<ResponseReverseGeocodingDto> =
        reverseGeocodingService.getLocationUsingLatLng(lat = lat, lon = lon)
}