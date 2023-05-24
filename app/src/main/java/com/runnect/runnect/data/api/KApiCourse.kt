package com.runnect.runnect.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.application.ApplicationClass
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object KApiCourse {

    private var retrofit: Retrofit? = null
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(AppInterceptor())
            .authenticator(
                TokenAuthenticator(
                    ApplicationClass.appContext
                )
            )
            .build()
    }

    //BASE API
    @OptIn(ExperimentalSerializationApi::class, InternalCoroutinesApi::class)
    fun getApiClient(): Retrofit? {
        synchronized(this) {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.RUNNECT_BASE_URL)
                    .client(client)
                    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                    .build()
            }
            return retrofit
        }
    }

    inline fun <reified T> create(): T = getApiClient()!!.create<T>(T::class.java)

    object ServicePool {
        val courseService = create<KCourseService>()


    }
}


