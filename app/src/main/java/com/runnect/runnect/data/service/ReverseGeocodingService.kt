package com.runnect.runnect.data.service

import com.runnect.runnect.BuildConfig
import com.runnect.runnect.data.dto.tmap.SearchResponseTmapDto
import com.runnect.runnect.data.dto.tmap.geocoding.ResponseReverseGeocodingDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReverseGeocodingService {

    @GET("/tmap/geo/reversegeocoding?")
    suspend fun getLocationUsingLatLng(
        @Header("appKey") appKey: String = BuildConfig.TMAP_API_KEY,
        @Query("version") version: Int = 1,
        @Query("callback") callback: String? = null,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("coordType") coordType: String? = null,
        @Query("addressType") addresstType: String? = null,
        ): Response<ResponseReverseGeocodingDto>
}
