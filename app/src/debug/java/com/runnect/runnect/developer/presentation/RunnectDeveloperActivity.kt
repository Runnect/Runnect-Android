package com.runnect.runnect.developer.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.runnect.runnect.R
import com.runnect.runnect.application.ApiMode
import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.data.service.TokenAuthenticator
import com.runnect.runnect.developer.enum.ServerStatus
import com.runnect.runnect.developer.presentation.custom.ServerStatusPreference
import com.runnect.runnect.presentation.mypage.setting.accountinfo.MySettingAccountInfoFragment
import com.runnect.runnect.util.custom.toast.RunnectToast
import com.runnect.runnect.util.extension.repeatOnStarted
import com.runnect.runnect.util.extension.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@AndroidEntryPoint
class RunnectDeveloperActivity : AppCompatActivity(R.layout.activity_runnect_developer) {

    class RunnectDeveloperFragment : PreferenceFragmentCompat() {

        private val viewModel: RunnectDeveloperViewModel by activityViewModels()

        private val clipboardManager: ClipboardManager? by lazy {
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_developer_menu, rootKey)
            activity?.apply {
                setStatusBarColor(window = window, true, R.color.white)
            }

            initUserInfo()
            initApiMode()
            initDeviceInfo()
            initDisplayInfo()
            initObserve()
            requestApi()
        }

        private fun requestApi() {
            with(viewModel) {
                checkProdServerStatus()
                checkTestServerStatus()
            }
        }

        private fun initObserve() {
            val prodPref = findPreference<ServerStatusPreference>("dev_pref_prod_server_status")
            val testPref = findPreference<ServerStatusPreference>("dev_pref_test_server_status")

            repeatOnStarted(
                {
                    viewModel.prodStatus.collect {
                        prodPref?.setServerStatus(ServerStatus.getStatus(it))
                    }
                },
                {
                    viewModel.testStatus.collect {
                        testPref?.setServerStatus(ServerStatus.getStatus(it))
                    }
                }
            )
        }

        private fun initUserInfo() {
            val ctx: Context = context ?: return
            val accessToken = PreferenceManager.getString(ctx, TokenAuthenticator.TOKEN_KEY_ACCESS) ?: ""
            val refreshToken = PreferenceManager.getString(ctx, TokenAuthenticator.TOKEN_KEY_REFRESH) ?: ""
            val combinedToken = "[Access Token]: $accessToken\n\n---\n\n[Refresh Token]: $refreshToken"

            setPreferenceSummary("dev_pref_key_access_token", accessToken)
            setPreferenceSummary("dev_pref_key_refresh_token", refreshToken)
            setPreferenceClickListener("dev_pref_key_share_tokens") {
                Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, combinedToken)
                }.let {
                    startActivity(Intent.createChooser(it, "Share tokens via:"))
                }
            }
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
                setOnPreferenceChangeListener { _, newValue ->
                    val selectItem = newValue.toString()
                    this.title = selectItem

                    PreferenceManager.apply {
                        setString(ctx, ApplicationClass.API_MODE, selectItem)
                        setString(ctx, MySettingAccountInfoFragment.TOKEN_KEY_ACCESS, "none")
                        setString(ctx, MySettingAccountInfoFragment.TOKEN_KEY_REFRESH, "none")
                    }

                    restartApplication(ctx)
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

        private fun setPreferenceClickListener(key: String, onClick: () -> Unit) {
            findPreference<Preference>(key)?.let { pref ->
                pref.setOnPreferenceClickListener {
                    onClick.invoke()
                    true
                }
            }
        }

        private fun copyToText(text: String): Boolean {
            val clipData = ClipData.newPlainText(CLIPBOARD_LABEL, text)
            clipboardManager?.setPrimaryClip(clipData)

            context?.let {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    RunnectToast.createToast(it, getString(R.string.dev_mode_copy_to_text)).show()
                }
            }

            return true
        }

        private fun restartApplication(context: Context) {
            val packageManager: PackageManager = context.packageManager
            val packageName = packageManager.getLaunchIntentForPackage(context.packageName)
            val component = packageName?.component

            lifecycleScope.launch(Dispatchers.Main) {
                RunnectToast.createToast(context, getString(R.string.dev_mode_require_restart)).show()
                delay(2000)

                Intent.makeRestartActivityTask(component).apply {
                    startActivity(this)
                    exitProcess(0)
                }
            }
        }

        companion object {
            private const val CLIPBOARD_LABEL = "keyword"
        }
    }
}