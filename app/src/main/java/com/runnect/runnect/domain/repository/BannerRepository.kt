package com.runnect.runnect.domain.repository

import com.runnect.runnect.domain.entity.PromotionBanner
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    suspend fun getPromotionBanners(): Flow<MutableList<PromotionBanner>>
}