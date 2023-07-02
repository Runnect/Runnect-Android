package com.runnect.runnect.application

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.R
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
        KakaoSdk.init(this,getString(R.string.kakao_native_app_key))
    }

    companion object {
        lateinit var appContext: Context
    }
}
