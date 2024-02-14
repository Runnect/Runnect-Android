package com.runnect.runnect.util.analytics

import android.content.Context
import android.os.Bundle
import android.util.StatsLog.logEvent
import com.google.android.gms.common.wrappers.InstantApps
import com.google.firebase.analytics.FirebaseAnalytics

object Analytics {
    private const val STATUS_INSTALLED = "installed"
    private const val STATUS_INSTANT = "instant"
    private const val ANALYTICS_USER_PROP = "app_type"

    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun initializeFirebaseAnalytics(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)

        // 현재 앱이 인스턴트 앱인지를 확인하고, 그 여부에 따라 UserProperty 다르게 세팅
        if (InstantApps.isInstantApp(context)) {
            setUserProperty(ANALYTICS_USER_PROP, STATUS_INSTANT)
        } else {
            setUserProperty(ANALYTICS_USER_PROP, STATUS_INSTALLED)
        }
    }

    private fun setUserProperty(property: String, value: String) {
        firebaseAnalytics?.setUserProperty(property, value)
    }

    fun logClickedItemEvent(eventName: String, itemName: String? = null) {
        val bundle = itemName?.let {
            Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_NAME, it)
            }
        }
        firebaseAnalytics?.logEvent(eventName, bundle)
    }

}
