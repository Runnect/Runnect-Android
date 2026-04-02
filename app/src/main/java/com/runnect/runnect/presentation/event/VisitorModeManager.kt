package com.runnect.runnect.presentation.event

import android.content.Context
import com.runnect.runnect.util.preference.AuthUtil.getAccessToken
import com.runnect.runnect.util.preference.StatusType.LoginStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitorModeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val isVisitorMode: Boolean
        get() {
            val accessToken = context.getAccessToken()
            return LoginStatus.getLoginStatus(accessToken) == LoginStatus.VISITOR
        }
}
