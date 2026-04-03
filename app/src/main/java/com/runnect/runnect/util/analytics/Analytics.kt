package com.runnect.runnect.util.analytics

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.wrappers.InstantApps
import com.google.firebase.analytics.FirebaseAnalytics

object Analytics {
    private const val STATUS_INSTALLED = "installed"
    private const val STATUS_INSTANT = "instant"
    private const val ANALYTICS_USER_PROP = "app_type"

    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun initializeFirebaseAnalytics(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)

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

    fun logEvent(eventName: String, params: Bundle? = null) {
        firebaseAnalytics?.logEvent(eventName, params)
    }

    fun logEvent(eventName: String, vararg params: Pair<String, Any?>) {
        val bundle = if (params.isNotEmpty()) {
            Bundle().apply {
                for ((key, value) in params) {
                    when (value) {
                        is String -> putString(key, value)
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        is Float -> putFloat(key, value)
                        is Double -> putDouble(key, value)
                        is Boolean -> putBoolean(key, value)
                        null -> {}
                    }
                }
            }
        } else null
        firebaseAnalytics?.logEvent(eventName, bundle)
    }
}
