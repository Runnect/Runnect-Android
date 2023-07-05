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
object RetrofitModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Runnect

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Tmap

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logger: HttpLoggingInterceptor,
        appInterceptor: AppInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient = OkHttpClient.Builder().addInterceptor(logger).addInterceptor(appInterceptor)
        .authenticator(tokenAuthenticator).build()

    @Provides
    @Singleton
    fun provideLogger(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(): TokenAuthenticator =
        TokenAuthenticator(ApplicationClass.appContext)

    @OptIn(ExperimentalSerializationApi::class, InternalCoroutinesApi::class)
    @Provides
    @Singleton
    @Runnect
    fun provideRunnectRetrofit(json: Json, client: OkHttpClient): Retrofit? =
        kotlinx.coroutines.internal.synchronized(this) {
            Retrofit.Builder().baseUrl(BuildConfig.RUNNECT_BASE_URL).client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
        }

    @OptIn(ExperimentalSerializationApi::class, InternalCoroutinesApi::class)
    @Provides
    @Singleton
    @Tmap
    fun provideTmapRetrofit(json: Json, client: OkHttpClient): Retrofit? =
        kotlinx.coroutines.internal.synchronized(this) {
            Retrofit.Builder().baseUrl(BuildConfig.TMAP_BASE_URL).client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
        }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }
}