package com.runnect.runnect.presentation

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.commit
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.runnect.runnect.BuildConfig.REMOTE_KEY_APP_VERSION
import com.runnect.runnect.R
import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityMainBinding
import com.runnect.runnect.presentation.coursemain.CourseMainFragment
import com.runnect.runnect.presentation.discover.DiscoverFragment
import com.runnect.runnect.presentation.mypage.MyPageFragment
import com.runnect.runnect.presentation.storage.StorageMainFragment
import com.runnect.runnect.presentation.storage.StorageScrapFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()
    private var isChangeToStorage: Boolean = false
    private var isChangeToDiscover: Boolean = false
    private var fragmentReplacementDirection: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRemoteConfig()
        checkVisitorMode()
        binding.vm = viewModel
        binding.lifecycleOwner = this
        checkIntentValue()
        initView()
        addListener()
    }

    private fun checkVisitorMode() {
        isVisitorMode =
            PreferenceManager.getString(ApplicationClass.appContext, TOKEN_KEY_ACCESS) == "visitor"
    }

    private fun checkIntentValue() {
        fragmentReplacementDirection = intent.getStringExtra(EXTRA_FRAGMENT_REPLACEMENT_DIRECTION)

        when (fragmentReplacementDirection) {
            "fromDrawCourse", "fromDeleteMyDrawDetail", "fromMyDrawDetail" -> isChangeToStorage =
                true

            "fromMyScrap", "fromCourseDetail" -> isChangeToDiscover = true
        }
    }

    private fun initView() {
        val selectedItemId = when {
            isChangeToStorage -> R.id.menu_main_storage.also { isChangeToStorage = false }
            isChangeToDiscover -> R.id.menu_main_discover.also { isChangeToDiscover = false }
            else -> R.id.menu_main_drawing
        }

        binding.btmNaviMain.menu.findItem(selectedItemId).isChecked = true
        changeFragment(selectedItemId)
    }

    private fun changeFragment(menuItemId: Int) {
        supportFragmentManager.commit {
            replace(
                R.id.fl_main, when (menuItemId) {
                    R.id.menu_main_drawing -> CourseMainFragment()
                    R.id.menu_main_storage -> StorageMainFragment()
                    R.id.menu_main_discover -> DiscoverFragment()
                    R.id.menu_main_my_page -> MyPageFragment()
                    else -> throw IllegalArgumentException("${this@MainActivity::class.java.simpleName} Not found menu item id")
                }
            )
        }
    }

    private fun addListener() {
        binding.btmNaviMain.setOnItemSelectedListener {
            changeFragment(it.itemId)
            Timber.tag("hu").d("fromDrawActivity when touch : $isChangeToStorage")
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
        val remoteConfig = Firebase.remoteConfig
        val localAppVersion =
            packageManager.getPackageInfo(packageName, 0).versionName //현재 설치된 앱의 버전 (versionName)
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 //1시간마다 최신 config를 가져오도록 설정
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync( //remote config 기본값 설정
            mapOf(
                REMOTE_KEY_APP_VERSION to DEFAULT_VERSION
            )
        )

        remoteConfig.fetchAndActivate().addOnCompleteListener {//remote confing에서 값 수신 및 활성화
            if (it.isSuccessful) { // fetch and activate 성공 }
                Timber.tag("remote_config").d("fetch and activate: Success")
                val updateAppVersion = remoteConfig.getString(REMOTE_KEY_APP_VERSION)
                Timber.tag("appVersion").d("local: $localAppVersion, remote: $updateAppVersion")
                if (localAppVersion != updateAppVersion) {
                    initUpdateDialog()
                }
            } else {// fetch and activate 실패
                Timber.tag("remote_config").d("fetch and activate: Fail")
            }
        }
    }

    private fun initUpdateDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(UPDATE_DIALOG_TITLE)
            .setMessage(UPDATE_DIALOG_MESSAGE)
            .setPositiveButton(UPDATE_DIALOG_BTN_TEXT) { _, _ -> loadPlayStore() }
            .setCancelable(false)
        builder.show()
    }

    private fun loadPlayStore() {
        val uri = "market://details?id=$packageName"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    companion object {

        const val UPDATE_DIALOG_TITLE = "업데이트"
        const val UPDATE_DIALOG_MESSAGE = "더 좋아진 Runnect 앱을 사용하시기 위해서는 최신 버전으로 업데이트가 필요합니다."
        const val UPDATE_DIALOG_BTN_TEXT = "업데이트"
        const val DEFAULT_VERSION = "0.0.0"

        const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"
        const val TOKEN_KEY_ACCESS = "access"

        var isVisitorMode = false
        var discoverFragment: DiscoverFragment? = null
        var storageScrapFragment: StorageScrapFragment? = null
        fun updateDiscoverFragment() {
            discoverFragment?.getRecommendCourses()
        }

        fun updateStorageScrap() {
            storageScrapFragment?.getCourse()
        }
    }
}


