package com.runnect.runnect.data.source.remote

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class RemoteBannerDataSource @Inject constructor(val bannerService: FirebaseFirestore) {
    fun getBannerData() = bannerService
}