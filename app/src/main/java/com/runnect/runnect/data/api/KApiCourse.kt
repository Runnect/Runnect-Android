package com.runnect.runnect.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.presentation.login.LoginActivity
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.IOException

object KApiCourse {

    //헤더에 accessToken과 refreshToken을 넣어야 하는데 accessToken이 만료되면 api 통신을 통해 갱신도 시켜줘야 함

    val accessToken = LoginActivity.accessToken
    val refreshToken = LoginActivity.refreshToken

    private var retrofit: Retrofit? = null
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //Machine ID를 헤더로 붙임
    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("accessToken", LoginActivity.accessToken)
                .addHeader("refreshToken", LoginActivity.refreshToken)
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
