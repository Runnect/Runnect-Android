package com.runnect.runnect.util.callback

import com.runnect.runnect.data.dto.DiscoverPromotionItem

interface OnBannerClick {
    fun selectBanner(item: DiscoverPromotionItem)
}