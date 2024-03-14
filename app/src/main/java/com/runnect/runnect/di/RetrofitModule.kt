package com.runnect.runnect.di

import com.google.android.gms.auth.api.Auth
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.data.service.*
import com.runnect.runnect.data.repository.*
import com.runnect.runnect.data.source.remote.*
import com.runnect.runnect.domain.*
import com.runnect.runnect.util.ApiLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
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

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Auth

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logger : HttpLoggingInterceptor,
        @Auth authInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .addInterceptor(authInterceptor)
        .build()


    @Provides
    @Singleton
    fun provideLogger(): HttpLoggingInterceptor = HttpLoggingInterceptor(ApiLogger()).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    @Auth
    fun provideAuthInterceptor(interceptor: AuthInterceptor): Interceptor = interceptor


    @OptIn(ExperimentalSerializationApi::class, InternalCoroutinesApi::class)
    @Provides
    @Singleton
    @Runnect
    fun provideRunnectRetrofit(json: Json, client: OkHttpClient): Retrofit {
        kotlinx.coroutines.internal.synchronized(this) {
            val baseUrl = ApplicationClass.getBaseUrl()
            val retrofit = Retrofit.Builder().baseUrl(baseUrl).client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
            return retrofit ?: throw RuntimeException("Retrofit creation failed.")
        }
    }

    @OptIn(ExperimentalSerializationApi::class, InternalCoroutinesApi::class)
    @Provides
    @Singleton
    @Tmap
    fun provideTmapRetrofit(json: Json, client: OkHttpClient): Retrofit {
        kotlinx.coroutines.internal.synchronized(this) {
            val retrofit = Retrofit.Builder().baseUrl(BuildConfig.TMAP_BASE_URL).client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
            return retrofit ?: throw RuntimeException("Retrofit creation failed.")
        }
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }
}