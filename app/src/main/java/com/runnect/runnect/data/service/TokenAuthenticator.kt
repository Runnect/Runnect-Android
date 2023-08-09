package com.runnect.runnect.data.service

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.mypage.setting.accountinfo.MySettingAccountInfoFragment
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class TokenAuthenticator(val context: Context) : Authenticator {
    private val BASE_URL = BuildConfig.RUNNECT_BASE_URL
    private var retrofit: Retrofit? = null


    @OptIn(DelicateCoroutinesApi::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            if (response.message == "Unauthorized") {
                clearToken()
                Toast.makeText(context,"장기간 미접속으로 인해 재로그인이 필요합니다.",Toast.LENGTH_LONG).show()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                return null
            }
            val newAccessToken = GlobalScope.async(Dispatchers.IO) { //Deferred
                getNewDeviceToken()
            }
            val isRefreshed = runBlocking {
                newAccessToken.await()
            }
            //토큰 재발급이 원활하게 이루어졌다면, 갱신된 토큰으로 재요청
            if (isRefreshed) {
                return getRequest(response)
            }
        }
        return null
    }
    private fun clearToken(){
        PreferenceManager.setString(context,
            TOKEN_KEY_ACCESS, "none")
        PreferenceManager.setString(context,
            TOKEN_KEY_REFRESH, "none")
    }

    private suspend inline fun getNewDeviceToken(): Boolean {
        return withContext(Dispatchers.IO) {
            //토큰 재발급
            callRefreshTokenApi()
        }
    }

    private suspend fun callRefreshTokenApi(): Boolean {
        runCatching {
            createRefreshService()!!.create(LoginService::class.java).getNewToken()
        }.onSuccess {
            PreferenceManager.setString(context, "access", it.data.accessToken)
            PreferenceManager.setString(context, "refresh", it.data.refreshToken)
            return true
        }.onFailure {
        }
        return false
    }

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(AppInterceptor())
            .build()
    }

    //BASE API
    @OptIn(ExperimentalSerializationApi::class, InternalCoroutinesApi::class)
    fun createRefreshService(): Retrofit? {
        kotlinx.coroutines.internal.synchronized(this) {
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

    //토큰 만료로 인해 거절된 요청 재시도
    private fun getRequest(response: Response): Request {
        return response.request
            .newBuilder()
            .removeHeader("accessToken")
            .removeHeader("refreshToken")
            .addHeader(
                "accessToken",
                PreferenceManager.getString(ApplicationClass.appContext, "access")!!
            )
            .addHeader(
                "refreshToken",
                PreferenceManager.getString(ApplicationClass.appContext, "refresh")!!
            )
            .build()
    }
    companion object {
        const val TOKEN_KEY_ACCESS = "access"
        const val TOKEN_KEY_REFRESH = "refresh"
    }
}