package com.runnect.runnect.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.data.api.*
import com.runnect.runnect.data.repository.*
import com.runnect.runnect.data.source.remote.*
import com.runnect.runnect.domain.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Singleton
    @Provides
    fun providePCourseService(@RetrofitModule.Runnect runnectRetrofit: Retrofit) = runnectRetrofit.create(PCourseService::class.java)

    @Singleton
    @Provides
    fun providePUserService(@RetrofitModule.Runnect runnectRetrofit: Retrofit) = runnectRetrofit.create(PUserService::class.java)

    @Singleton
    @Provides
    fun provideLoginService(@RetrofitModule.Runnect runnectRetrofit: Retrofit) = runnectRetrofit.create(LoginService::class.java)

    @Singleton
    @Provides
    fun provideKSearchService(@RetrofitModule.Tmap tmapRetrofit: Retrofit) = tmapRetrofit.create(KSearchService::class.java)

}