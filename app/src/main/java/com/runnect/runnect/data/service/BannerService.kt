package com.runnect.runnect.data.service

import com.runnect.runnect.data.dto.response.ResponseGetBanner
import retrofit2.http.GET

interface BannerService {

    @GET("/api/banner")
    suspend fun getBanners(): Result<ResponseGetBanner>
}
