package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.DiscoverPromotionItemDTO
import com.runnect.runnect.data.source.remote.RemoteBannerDataSource
import com.runnect.runnect.domain.BannerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BannerRepositoryImpl @Inject constructor(private val remoteBannerDataSource: RemoteBannerDataSource) :
    BannerRepository {
    override suspend fun getBannerData(): Flow<MutableList<DiscoverPromotionItemDTO>> =
        remoteBannerDataSource.getBannerFlow()
}