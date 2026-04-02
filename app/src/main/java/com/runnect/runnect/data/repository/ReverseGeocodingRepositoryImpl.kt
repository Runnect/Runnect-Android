package com.runnect.runnect.data.repository

import com.runnect.runnect.data.network.mapToFlowResult
import com.runnect.runnect.data.source.remote.RemoteReverseGeocodingDataSource
import com.runnect.runnect.domain.entity.LocationData
import com.runnect.runnect.domain.repository.ReverseGeocodingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReverseGeocodingRepositoryImpl @Inject constructor(private val reverseGeocodingDataSource: RemoteReverseGeocodingDataSource) :
    ReverseGeocodingRepository {
    override suspend fun getLocationInfoUsingLatLng(
        lat: Double,
        lon: Double
    ): Flow<Result<LocationData>> =
        reverseGeocodingDataSource.getLocationInfoUsingLatLng(lat = lat, lon = lon)
            .mapToFlowResult {
                it.toLocationData()
            }
}