package com.runnect.runnect.developer

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.runnect.runnect.R
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.data.service.TokenAuthenticator

class RunnectDeveloperActivity : AppCompatActivity(R.layout.activity_runnect_developer) {

    class RunnectDeveloperFragment : PreferenceFragmentCompat() {

        private val DEF_TYPE = "dimen"
        private val DEF_PACKAGE = "android"
        private val CLIPBOARD_LABEL = "keyword"
        private val STATUS_BAR_HEIGHT = "status_bar_height"
        private val NAVIGATION_BAR_HEIGHT = "navigation_bar_height"

        private val clipboardManager: ClipboardManager? by lazy {
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_developer_menu, rootKey)

            initUserInfo()
            initDeviceInfo()
            initDisplayInfo()
        }

        private fun initUserInfo() {
            val ctx: Context = context ?: return
            val accessToken = PreferenceManager.getString(ctx, TokenAuthenticator.TOKEN_KEY_ACCESS) ?: ""
            val refreshToken = PreferenceManager.getString(ctx, TokenAuthenticator.TOKEN_KEY_REFRESH) ?: ""

            setPreferenceSummary("dev_pref_key_access_token", accessToken)
            setPreferenceSummary("dev_pref_key_refresh_token", refreshToken)
        }

        private fun initDeviceInfo() {
            setPreferenceSummary("dev_pref_android_version", Build.VERSION.RELEASE)
            setPreferenceSummary("dev_pref_model_name", "${Build.BRAND} ${Build.MODEL}")
            setPreferenceSummary("dev_pref_sdk_version", "${Build.VERSION.SDK_INT}")
        }

        private fun initDisplayInfo() {
            val metrics = activity?.resources?.displayMetrics ?: return
            val statusBarHeight = getStatusBarHeight()
            val naviBarHeight = getNaviBarHeight()

            with(metrics) {
                setPreferenceSummary("dev_pref_display_ratio", "$widthPixels x ${heightPixels + statusBarHeight + naviBarHeight}")
                setPreferenceSummary("dev_pref_display_density", "${densityDpi}dp")
                setPreferenceSummary("dev_pref_display_resource_bucket", getDeviceResourseBucket(this))
            }
        }

        private fun getDeviceResourseBucket(metrics: DisplayMetrics): String {
            val densityDpi = metrics.densityDpi

            return when {
                densityDpi <= DisplayMetrics.DENSITY_LOW -> "ldpi"
                densityDpi <= DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
                densityDpi <= DisplayMetrics.DENSITY_HIGH -> "hdpi"
                densityDpi <= DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
                densityDpi <= DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
                densityDpi <= DisplayMetrics.DENSITY_XXXHIGH -> "xxxhdpi"
                else -> "unknown"
            }
        }

        @SuppressLint("InternalInsetResource", "DiscouragedApi")
        fun getStatusBarHeight(): Int {
            val resourceId = resources.getIdentifier(STATUS_BAR_HEIGHT, DEF_TYPE, DEF_PACKAGE)
            return if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else 0
        }

        @SuppressLint("InternalInsetResource", "DiscouragedApi")
        fun getNaviBarHeight(): Int {
            val resourceId = resources.getIdentifier(NAVIGATION_BAR_HEIGHT, DEF_TYPE, DEF_PACKAGE)
            return if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else 0
        }

        private fun setPreferenceSummary(key: String, value: String) {
            findPreference<Preference>(key)?.let { pref ->
                pref.summary = value
                pref.setOnPreferenceClickListener {
                    copyToText(value)
                }
            }
        }

        private fun copyToText(text: String): Boolean {
            val clipData = ClipData.newPlainText(CLIPBOARD_LABEL, text)
            clipboardManager?.setPrimaryClip(clipData)

            return true
        }
    }
}