package com.runnect.runnect.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.data.network.calladapter.ApiResultCallAdapterFactory
import com.runnect.runnect.data.network.interceptor.ResponseInterceptor
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    annotation class RetrofitV2

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Tmap

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HttpClientV2

    @Provides
    @Singleton
    @HttpClient
    fun provideOkHttpClient(
        logger: HttpLoggingInterceptor,
        appInterceptor: AppInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .addInterceptor(appInterceptor)
        .authenticator(tokenAuthenticator)
        .build()

    @Provides
    @Singleton
    @HttpClientV2
    fun provideOkHttpClientV2(
        logger: HttpLoggingInterceptor,
        appInterceptor: AppInterceptor,
        responseInterceptor: ResponseInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .addInterceptor(appInterceptor)
        .addInterceptor(responseInterceptor)
        .authenticator(tokenAuthenticator)
        .build()

    @Provides
    @Singleton
    fun provideLogger(): HttpLoggingInterceptor = HttpLoggingInterceptor(ApiLogger()).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideAppInterceptor(): AppInterceptor = AppInterceptor()

    @Provides
    @Singleton
    fun provideResponseInterceptor(): ResponseInterceptor = ResponseInterceptor()

    @Provides
    @Singleton
    fun provideTokenAuthenticator(): TokenAuthenticator =
        TokenAuthenticator(ApplicationClass.appContext)

    @OptIn(ExperimentalSerializationApi::class, InternalCoroutinesApi::class)
    @Provides
    @Singleton
    @Runnect
    fun provideRunnectRetrofit(json: Json, @HttpClient client: OkHttpClient): Retrofit {
        kotlinx.coroutines.internal.synchronized(this) {
            val baseUrl = ApplicationClass.getBaseUrl()
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(
                    json.asConverterFactory("application/json".toMediaType())
                ).build()

            return retrofit ?: throw RuntimeException("Retrofit creation failed.")
        }
    }

    @OptIn(ExperimentalSerializationApi::class, InternalCoroutinesApi::class)
    @Provides
    @Singleton
    @RetrofitV2
    fun provideRunnectRetrofitV2(
        @HttpClientV2 client: OkHttpClient
    ): Retrofit {
        val baseUrl = ApplicationClass.getBaseUrl()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResultCallAdapterFactory.create())
            .build()

        return retrofit ?: throw RuntimeException("Retrofit creation failed.")
    }

    @OptIn(ExperimentalSerializationApi::class, InternalCoroutinesApi::class)
    @Provides
    @Singleton
    @Tmap
    fun provideTmapRetrofit(json: Json, @HttpClient client: OkHttpClient): Retrofit {
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