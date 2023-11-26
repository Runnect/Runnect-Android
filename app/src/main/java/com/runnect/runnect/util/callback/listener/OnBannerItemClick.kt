package com.runnect.runnect.util.callback.listener

import com.runnect.runnect.domain.entity.DiscoverPromotionBanner

interface OnBannerItemClick {
    fun selectBanner(item: DiscoverPromotionBanner)
}
