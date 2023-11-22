package com.runnect.runnect.data.source.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.runnect.runnect.data.dto.DiscoverPromotionItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class RemoteBannerDataSource @Inject constructor(val bannerService: FirebaseFirestore) {
    private val promotionImages = mutableListOf<DiscoverPromotionItem>()
    fun getBannerFlow(): Flow<MutableList<DiscoverPromotionItem>> = callbackFlow {
        bannerService.collection("data")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.tag("FirebaseData").d("fail : ${e.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    promotionImages.clear()
                    for (document in snapshot) {
                        promotionImages.add(
                            DiscoverPromotionItem(
                                index = document.getLong("index")!!.toInt(),
                                imageUrl = document.getString("imageUrl").toString(),
                                linkUrl = document.getString("linkUrl").toString()
                            )
                        )
                    }
                    Timber.tag("실시간-파베-data").d("promotionImages : $promotionImages")
                    trySend(promotionImages)
                }
            }
        awaitClose()
    }
}
