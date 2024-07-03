package com.runnect.runnect.developer.di

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.runnect.runnect.data.repository.*
import com.runnect.runnect.data.service.*
import com.runnect.runnect.data.source.remote.*
import com.runnect.runnect.developer.data.service.ServerStatusService
import com.runnect.runnect.di.RetrofitModule
import com.runnect.runnect.domain.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DebugServiceModule {

    @Singleton
    @Provides
    fun provideBannerService() = Firebase.firestore

    @Singleton
    @Provides
    fun provideServerStatusService(@RetrofitModule.RetrofitV2 retrofitV2: Retrofit) =
        retrofitV2.create(ServerStatusService::class.java)
}