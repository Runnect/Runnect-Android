package com.runnect.runnect.util.callback.listener

import com.runnect.runnect.domain.entity.DiscoverBanner

interface OnBannerItemClick {
    fun selectBanner(item: DiscoverBanner)
}
