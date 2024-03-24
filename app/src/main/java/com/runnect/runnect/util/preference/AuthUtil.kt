package com.runnect.runnect.util.preference

import android.content.Context
import com.runnect.runnect.application.PreferenceManager

object AuthUtil {

    fun Context.getAccessToken(): String {
        return PreferenceManager.getString(
            this,
            TOKEN_KEY_ACCESS
        ) ?: ""
    }

    fun Context.getNewToken(): String {
        return PreferenceManager.getString(
            this,
            TOKEN_KEY_REFRESH
        ) ?: ""
    }

    fun Context.saveToken(accessToken: String, refreshToken: String) {
        PreferenceManager.setString(this, TOKEN_KEY_ACCESS, accessToken)
        PreferenceManager.setString(this, TOKEN_KEY_REFRESH, refreshToken)
    }


    private const val TOKEN_KEY_ACCESS = "access"
    private const val TOKEN_KEY_REFRESH = "refresh"

}