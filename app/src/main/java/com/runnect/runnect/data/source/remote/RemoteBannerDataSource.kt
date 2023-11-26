package com.runnect.runnect.data.source.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.runnect.runnect.domain.entity.DiscoverPromotionBanner
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class RemoteBannerDataSource @Inject constructor(
    private val bannerService: FirebaseFirestore
) {
    private val banners = mutableListOf<DiscoverPromotionBanner>()

    fun getPromotionBanners(): Flow<MutableList<DiscoverPromotionBanner>> = callbackFlow {
        bannerService.collection("data")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.tag("FirebaseData").d("fail : ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    banners.clear()
                    for (document in snapshot) {
                        banners.add(
                            DiscoverPromotionBanner(
                                index = document.getLong("index")!!.toInt(),
                                imageUrl = document.getString("imageUrl").toString(),
                                linkUrl = document.getString("linkUrl").toString()
                            )
                        )
                    }
                    Timber.tag("실시간-파베-data").d("promotion banners : $banners")
                    trySend(banners)
                }
            }
        awaitClose()
    }
}
