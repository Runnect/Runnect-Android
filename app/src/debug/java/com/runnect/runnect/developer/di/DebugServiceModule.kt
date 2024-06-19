package com.runnect.runnect.developer.di

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.runnect.runnect.data.repository.*
import com.runnect.runnect.data.service.*
import com.runnect.runnect.data.source.remote.*
import com.runnect.runnect.domain.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DebugServiceModule {

    @Singleton
    @Provides
    fun provideBannerService() = Firebase.firestore
}