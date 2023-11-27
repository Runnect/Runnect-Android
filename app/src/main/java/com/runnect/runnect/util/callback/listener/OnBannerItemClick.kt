package com.runnect.runnect.util.callback.listener

import com.runnect.runnect.domain.entity.PromotionBanner

interface OnBannerItemClick {
    fun selectBanner(item: PromotionBanner)
}
