package com.runnect.runnect.application

import android.app.Application
import android.content.Context
import com.runnect.runnect.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
    }
}
