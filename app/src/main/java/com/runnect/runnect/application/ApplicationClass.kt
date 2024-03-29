package com.runnect.runnect.application

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.R
import com.runnect.runnect.util.analytics.Analytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        appContext = applicationContext
        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))
        initApiMode()
        initAnalytics()
    }

    private fun initApiMode() {
        val currentApi = ApiMode.getCurrentApiMode(appContext)
        PreferenceManager.setString(appContext, API_MODE, currentApi.name)
    }

    private fun initAnalytics() {
        Analytics.initializeFirebaseAnalytics(applicationContext)
    }

    companion object {
        lateinit var appContext: Context
        const val API_MODE = "API_MODE"

        fun getBaseUrl(): String {
            return when {
                !BuildConfig.DEBUG -> BuildConfig.RUNNECT_PROD_URL
                !::appContext.isInitialized -> BuildConfig.RUNNECT_PROD_URL
                else -> {
                    val mode = ApiMode.getCurrentApiMode(appContext)
                    when (mode) {
                        ApiMode.JAVA -> BuildConfig.RUNNECT_PROD_URL
                        ApiMode.TEST -> BuildConfig.RUNNECT_DEV_URL
                        else -> BuildConfig.RUNNECT_NODE_URL
                    }
                }
            }
        }
    }
}
