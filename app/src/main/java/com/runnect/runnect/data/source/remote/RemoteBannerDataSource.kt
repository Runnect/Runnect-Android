package com.runnect.runnect.data.source.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.runnect.runnect.domain.entity.DiscoverBanner
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class RemoteBannerDataSource @Inject constructor(
    private val bannerService: FirebaseFirestore
) {
    private val banners = mutableListOf<DiscoverBanner>()

    fun getDiscoverBanners(): Flow<MutableList<DiscoverBanner>> = callbackFlow {
        bannerService.collection("data")
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Timber.e("Firebase Firestore Exception: ${exception.message}")
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    banners.clear()
                    for (document in querySnapshot) {
                        banners.add(
                            DiscoverBanner(
                                index = document.getLong("index")!!.toInt(),
                                imageUrl = document.getString("imageUrl").toString(),
                                linkUrl = document.getString("linkUrl").toString()
                            )
                        )
                    }
                    Timber.d("SUCCESS GET DISCOVER BANNERS : $banners")
                    trySend(banners)
                }
            }
        awaitClose()
    }
}
