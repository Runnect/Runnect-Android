package com.runnect.runnect.developer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.runnect.runnect.R
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.data.service.TokenAuthenticator

class RunnectDeveloperActivity : AppCompatActivity(R.layout.activity_runnect_developer) {

    class RunnectDeveloperFragment : PreferenceFragmentCompat() {

        private val clipboardManager: ClipboardManager? by lazy {
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_developer_menu, rootKey)

            initUserInfo()
            initDeviceInfo()
        }

        private fun initUserInfo(){
            val ctx:Context = context ?: return
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

        private fun setPreferenceSummary(key: String, value: String) {
            findPreference<Preference>(key)?.let { pref ->
                pref.summary = value
                pref.setOnPreferenceClickListener {
                    copyToText(value)
                }
            }
        }

        private fun copyToText(text: String): Boolean {
            val clipData = ClipData.newPlainText("keyword", text)
            clipboardManager?.setPrimaryClip(clipData)

            return true
        }
    }
}