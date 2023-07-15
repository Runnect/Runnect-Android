package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.DiscoverPromotionItemDTO

interface BannerRepository {
    suspend fun getBannerData(): MutableList<DiscoverPromotionItemDTO>
}