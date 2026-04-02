package com.runnect.runnect.presentation

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.runnect.runnect.BuildConfig.REMOTE_KEY_APP_VERSION
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityMainBinding
import com.runnect.runnect.presentation.event.VisitorModeManager
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName
import com.runnect.runnect.util.analytics.EventName.EVENT_VIEW_HOME
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    @Inject
    lateinit var visitorModeManager: VisitorModeManager

    private var isChangeToStorage: Boolean = false
    private var isChangeToDiscover: Boolean = false
    private var fragmentReplacementDirection: String? = null
    private lateinit var viewPagerAdapter: MainPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Analytics.logClickedItemEvent(EVENT_VIEW_HOME)
        initRemoteConfig()
        checkIntentValue()
        initView()
        addListener()
    }

    private fun checkIntentValue() {
        fragmentReplacementDirection = intent.getStringExtra(EXTRA_FRAGMENT_REPLACEMENT_DIRECTION)

        when (fragmentReplacementDirection) {
            "fromDrawCourse", "fromDeleteMyDrawDetail", "fromMyDrawDetail" -> isChangeToStorage = true
            "fromMyScrap", "fromCourseDetail" -> isChangeToDiscover = true
        }
    }

    private fun initView() {
        setupViewPager()
        val selectedPosition = when {
            isChangeToStorage -> 1.also { isChangeToStorage = false }
            isChangeToDiscover -> 2.also { isChangeToDiscover = false }
            else -> 0
        }
        binding.vpMain.currentItem = selectedPosition
        updateBottomNavigationSelection(selectedPosition)
    }

    private fun setupViewPager() {
        viewPagerAdapter = MainPager(this)
        binding.vpMain.apply {
            adapter = viewPagerAdapter
            isUserInputEnabled = false
            offscreenPageLimit = 3
        }
    }

    private fun changeFragment(menuItemId: Int) {
        val position = getPositionFromMenuId(menuItemId)
        logClickEvent(menuItemId)
        binding.vpMain.setCurrentItem(position, false)
    }

    private fun getPositionFromMenuId(menuItemId: Int): Int {
        return when (menuItemId) {
            R.id.menu_main_drawing -> 0
            R.id.menu_main_storage -> 1
            R.id.menu_main_discover -> 2
            R.id.menu_main_my_page -> 3
            else -> 0
        }
    }

    private fun updateBottomNavigationSelection(position: Int) {
        val menuItemId = when (position) {
            0 -> R.id.menu_main_drawing
            1 -> R.id.menu_main_storage
            2 -> R.id.menu_main_discover
            3 -> R.id.menu_main_my_page
            else -> R.id.menu_main_drawing
        }
        binding.btmNaviMain.menu.findItem(menuItemId).isChecked = true
    }

    private fun logClickEvent(menuItemId: Int) {
        val isVisitor = visitorModeManager.isVisitorMode
        with(EventName) {
            when (menuItemId) {
                R.id.menu_main_drawing -> if (isVisitor) EVENT_CLICK_JOIN_IN_COURSE_DRAWING else EVENT_CLICK_NAV_COURSE_DRAWING
                R.id.menu_main_storage -> if (isVisitor) EVENT_CLICK_JOIN_IN_STORAGE else EVENT_CLICK_NAV_STORAGE
                R.id.menu_main_discover -> if (isVisitor) EVENT_CLICK_JOIN_IN_COURSE_DISCOVERY else EVENT_CLICK_NAV_COURSE_DISCOVERY
                R.id.menu_main_my_page -> if (isVisitor) EVENT_CLICK_JOIN_IN_MY_PAGE else EVENT_CLICK_NAV_MY_PAGE
                else -> ""
            }.let(Analytics::logClickedItemEvent)
        }
    }

    private fun addListener() {
        binding.btmNaviMain.setOnItemSelectedListener {
            changeFragment(it.itemId)
            true
        }
    }

    fun getBottomNavMain(): View {
        return binding.btmNaviMain
    }

    fun getBtnDeleteCourseMain(): View {
        return binding.btnDeleteCourseMain
    }

    private fun initRemoteConfig() {
        Firebase.remoteConfig.run {
            val localAppVersion = packageManager.getPackageInfo(packageName, 0).versionName
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = REMOTE_CONFIG_FETCH_INTERVAL_SECONDS
            }
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync( //remote config 기본값 설정
                mapOf(REMOTE_KEY_APP_VERSION to getString(R.string.default_version))
            )

            fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updateAppVersion = getString(REMOTE_KEY_APP_VERSION)
                    if (localAppVersion != updateAppVersion) {
                        initUpdateDialog()
                    }
                }
            }
        }
    }

    private fun initUpdateDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.update_dialog_title)
            setMessage(R.string.update_dialog_message)
            setPositiveButton(R.string.update_dialog_btn_text) { _, _ -> loadPlayStore() }
            setCancelable(false)
            show()
        }
    }

    private fun loadPlayStore() {
        val uri = "market://details?id=$packageName"
        val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
        startActivity(intent)
    }

    companion object {
        const val REMOTE_CONFIG_FETCH_INTERVAL_SECONDS = 3600L
        const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"
    }
}
