package com.runnect.runnect.util.callback

import com.runnect.runnect.data.dto.DiscoverPromotionItem

interface OnBannerItemClick {
    fun selectBanner(item: DiscoverPromotionItem)
}