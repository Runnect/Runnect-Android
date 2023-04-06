package com.runnect.runnect.data.api

import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.application.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException

class AppInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val newRequest = request().newBuilder()
            .addHeader(
                "accessToken",
                PreferenceManager.getString(ApplicationClass.appContext, "access")!!
            )
            .addHeader(
                "refreshToken",
                PreferenceManager.getString(ApplicationClass.appContext, "refresh")!!
            )
            .build()
        proceed(newRequest)
    }
}