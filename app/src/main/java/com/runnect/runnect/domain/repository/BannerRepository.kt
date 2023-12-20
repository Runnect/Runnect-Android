package com.runnect.runnect.domain.repository

import com.runnect.runnect.domain.entity.DiscoverBanner
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    suspend fun getDiscoverBanners(): Flow<MutableList<DiscoverBanner>>
}