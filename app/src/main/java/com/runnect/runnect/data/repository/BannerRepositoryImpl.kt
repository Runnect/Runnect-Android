package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.DiscoverPromotionItemDTO
import com.runnect.runnect.data.source.remote.RemoteBannerDataSource
import com.runnect.runnect.domain.BannerRepository
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BannerRepositoryImpl @Inject constructor(private val remoteBannerDataSource: RemoteBannerDataSource) :
    BannerRepository {

    override suspend fun getBannerData(): MutableList<DiscoverPromotionItemDTO> = suspendCoroutine { continuation ->
        val promotionImages = mutableListOf<DiscoverPromotionItemDTO>()

        remoteBannerDataSource.getBannerData()
            .collection("data")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.tag("FirebaseData").d("fail : ${e.message}")
                    continuation.resumeWith(Result.failure(e))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    for (document in snapshot) {
                        promotionImages.add(
                            DiscoverPromotionItemDTO(
                                index = document.getLong("index")!!.toInt(),
                                imageUrl = document.getString("imageUrl").toString(),
                                linkUrl = document.getString("linkUrl").toString()
                            )
                        )
                    }
                    Timber.tag("FirebaseRepo").d("promotionImages : $promotionImages")
                    continuation.resume(promotionImages)
                }
            }
    }
}