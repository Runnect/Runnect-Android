package com.runnect.runnect.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.presentation.login.LoginActivity
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Retrofit

object PApiClient {

    val accessToken = LoginActivity.accessToken
    val refreshToken = LoginActivity.refreshToken

    private const val BASE_URL = BuildConfig.RUNNECT_BASE_URL
    private var retrofit: Retrofit? = null
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //Machine ID를 헤더로 붙임
    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("accessToken", accessToken)
                .addHeader("refreshToken", refreshToken)
                .build()
            proceed(newRequest)
        }
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(AppInterceptor())
            .build()
    }

    //BASE API
    @OptIn(ExperimentalSerializationApi::class, InternalCoroutinesApi::class)
    fun getApiClient(): Retrofit? {
        synchronized(this) {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                    .build()
            }
            return retrofit
        }
    }

}