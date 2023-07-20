package com.runnect.runnect.data.service

import com.runnect.runnect.BuildConfig
import com.runnect.runnect.data.dto.tmap.SearchResponseTmapDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchService {

    @GET("/tmap/pois")
    suspend fun getSearchLocation(
        @Header("appKey") appKey: String = BuildConfig.TMAP_API_KEY,
        @Query("version") version: Int = 1,
        @Query("callback") callback: String? = null,
        @Query("count") count: Int = 20,
        @Query("searchKeyword") keyword: String,
        @Query("areaLLCode") areaLLCode: String? = null,
        @Query("areaLMCode") areaLMCode: String? = null,
        @Query("resCoordType") resCoordType: String? = null,
        @Query("searchType") searchType: String? = null,
        @Query("multiPoint") multiPoint: String? = null,
        @Query("searchtypCd") searchtypCd: String? = null,
        @Query("radius") radius: String? = null,
        @Query("reqCoordType") reqCoordType: String? = null,
        @Query("centerLon") centerLon: String? = null,
        @Query("centerLat") centerLat: String? = null,
    ): Response<SearchResponseTmapDto>
}
