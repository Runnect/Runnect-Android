package com.runnect.runnect.domain

import com.runnect.runnect.domain.entity.DiscoverPromotionBanner
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    suspend fun getPromotionBanners(): Flow<MutableList<DiscoverPromotionBanner>>
}