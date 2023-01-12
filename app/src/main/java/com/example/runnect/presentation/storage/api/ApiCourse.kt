package com.example.runnect.presentation.storage.api

import com.example.runnect.data.api.Url
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
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

object ApiCourse {


    private var retrofit: Retrofit? = null
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //Machine ID를 헤더로 붙임
    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("machineId", "SEONHEUI")
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
                    .baseUrl(Url.BASE_RUNNET)
                    .client(client)
                    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                    .build()
            }
            return retrofit
        }
    }

    inline fun <reified T> create(): T = getApiClient()!!.create<T>(T::class.java)

    //이러면 이렇게 생성하는 모든 레트로핏 객체 header에 machineId가 붙을 거 같은데 오류 안 뜨나?
    object ServicePool {
        val courseService = create<CourseService>()


    }
}
