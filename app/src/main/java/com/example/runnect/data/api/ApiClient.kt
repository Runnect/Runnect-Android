package com.example.runnect.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ApiClient {
    private const val BASE_URL = "https://run.mocky.io/v3/"
    private var retrofit: Retrofit? = null

    @OptIn(ExperimentalSerializationApi::class)
    fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
        }
        return retrofit!!
    }

    inline fun <reified T> create(): T = getRetrofit().create<T>(T::class.java)

    object ServicePool {
        val getRewardService = create<GetRewardService>()
    }
}