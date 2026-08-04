package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.service.BannerService
import com.runnect.runnect.domain.entity.DiscoverBanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class RemoteBannerDataSource @Inject constructor(
    private val bannerService: BannerService
) {
    fun getDiscoverBanners(): Flow<MutableList<DiscoverBanner>> = flow {
        val banners = bannerService.getBanners().getOrThrow().banners.map { banner ->
            DiscoverBanner(
                index = banner.index,
                imageUrl = banner.imageUrl,
                linkUrl = banner.linkUrl
            )
        }.toMutableList()

        Timber.d("SUCCESS GET DISCOVER BANNERS : $banners")
        emit(banners)
    }
}
