package com.runnect.runnect.developer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.runnect.runnect.R
import com.runnect.runnect.application.ApiMode
import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.data.service.TokenAuthenticator
import com.runnect.runnect.presentation.mypage.setting.accountinfo.MySettingAccountInfoFragment
import com.runnect.runnect.util.custom.toast.RunnectToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class RunnectDeveloperActivity : AppCompatActivity(R.layout.activity_runnect_developer) {

    class RunnectDeveloperFragment : PreferenceFragmentCompat() {

        private val CLIPBOARD_LABEL = "keyword"

        private val clipboardManager: ClipboardManager? by lazy {
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_developer_menu, rootKey)

            initUserInfo()
            initApiMode()
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

        private fun initApiMode() {
            val ctx:Context = context ?: ApplicationClass.appContext
            val currentApi = ApiMode.getCurrentApiMode(ctx)

            findPreference<ListPreference>("dev_pref_key_api_mode")?.apply {
                val entries = ApiMode.values().map { it.name }.toTypedArray()
                val selectIndex = ApiMode.values().indexOf(currentApi)

                this.entries = entries
                this.entryValues = entries

                title = currentApi.name
                setValueIndex(selectIndex)

                setOnPreferenceChangeListener { preference, newValue ->
                    val selectItem = newValue.toString()
                    this.title = selectItem

                    PreferenceManager.apply {
                        setString(ctx, ApplicationClass.API_MODE, selectItem)
                        setString(ctx, MySettingAccountInfoFragment.TOKEN_KEY_ACCESS, "none")
                        setString(ctx, MySettingAccountInfoFragment.TOKEN_KEY_REFRESH, "none")
                    }

                    destroyApp(ctx)
                    true
                }
            }
        }

        private fun initDeviceInfo() {
            setPreferenceSummary("dev_pref_android_version", Build.VERSION.RELEASE)
            setPreferenceSummary("dev_pref_model_name", "${Build.BRAND} ${Build.MODEL}")
            setPreferenceSummary("dev_pref_sdk_version", "${Build.VERSION.SDK_INT}")
        }

        private fun initDisplayInfo() {
            val metrics = activity?.resources?.displayMetrics ?: return
            val windowManager = activity?.windowManager ?: return
            val statusBarHeight = getStatusBarHeight(windowManager)
            val naviBarHeight = getNaviBarHeight(windowManager)

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

        private fun getStatusBarHeight(windowManager: WindowManager): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics = windowManager.currentWindowMetrics
                val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars())
                insets.top
            } else {
                0
            }
        }

        private fun getNaviBarHeight(windowManager: WindowManager): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics = windowManager.currentWindowMetrics
                val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars())
                insets.bottom
            } else {
                0
            }
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

        private fun destroyApp(context: Context) {
            lifecycleScope.launch(Dispatchers.Main) {
                RunnectToast.createToast(context, getString(R.string.dev_mode_require_restart)).show()
                delay(3000)

                activity?.finishAffinity() //루트액티비티 종료
                exitProcess(0)
            }
        }
    }
}