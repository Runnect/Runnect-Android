package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.DiscoverPromotionItem
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    suspend fun getBannerData(): Flow<MutableList<DiscoverPromotionItem>>
}