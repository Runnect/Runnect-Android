package com.runnect.runnect.util.callback

import com.runnect.runnect.data.dto.DiscoverPromotionItemDTO

interface OnBannerClick {
    fun selectBanner(item: DiscoverPromotionItemDTO)
}