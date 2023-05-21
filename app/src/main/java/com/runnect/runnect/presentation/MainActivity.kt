package com.runnect.runnect.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.runnect.runnect.R
import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityMainBinding
import com.runnect.runnect.presentation.coursemain.CourseMainFragment
import com.runnect.runnect.presentation.discover.DiscoverFragment
import com.runnect.runnect.presentation.mypage.MyPageFragment
import com.runnect.runnect.presentation.storage.StorageMainFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()

    var isChangeToStorage: Boolean = false
    var isChangeToDiscover: Boolean = false
    var fromEndRunActivity: String? = ""
    var fromDeleteMyDraw: Boolean = false
    var fromDrawMyCourse: Boolean = false
    var fromScrapFragment: Boolean = false

    companion object {
        var isVisitorMode = false
        var discoverFragment: DiscoverFragment? = null
        fun updateDiscoverFragment() {
            discoverFragment?.getRecommendCourses()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkVisitorMode()

        binding.vm = viewModel
        binding.lifecycleOwner = this
        CheckIntentValue()
        initView()
        addListener() //이게 있어야 changeFragment를 돌릴 수 있음
    }

    private fun checkVisitorMode() {
        isVisitorMode =
            PreferenceManager.getString(ApplicationClass.appContext, "access")!! == "visitor"
    }

    private fun CheckIntentValue() {

        fromDeleteMyDraw = intent.getBooleanExtra("fromDeleteMyDraw", false)
        fromDrawMyCourse = intent.getBooleanExtra("fromDrawActivity", false)

        if (fromDeleteMyDraw) {
            isChangeToStorage = true
        }
        if (fromDrawMyCourse) {
            isChangeToStorage = true
        }

        fromScrapFragment = intent.getBooleanExtra("fromScrapFragment", false)

        if (fromScrapFragment) {
            isChangeToDiscover = true
        }

        fromEndRunActivity = intent.getStringExtra("dataFrom")

        if (fromEndRunActivity == "myDraw") {
            isChangeToStorage = true
        }
        if (fromEndRunActivity == "detail") {
            isChangeToDiscover = true
        }
    }

    private fun initView() {
        if (!isChangeToStorage && !isChangeToDiscover) {//1. 앱 처음 빌드
            changeFragment(R.id.menu_main_drawing)
            Timber.tag("hu").d("fromDrawActivity (default) : ${isChangeToStorage}")
        } else if (isChangeToStorage) { // 2. 다른 액티비티에서 넘어오는 경우 (from DrawActivity)
            isChangeToStorage = false
            binding.btmNaviMain.menu.findItem(R.id.menu_main_storage).isChecked = true
            changeFragment(R.id.menu_main_storage)
            Timber.tag("hu").d("fromDrawActivity (true->false): ${isChangeToStorage}")
        } else if (isChangeToDiscover) { // 2. 다른 액티비티에서 넘어오는 경우 (from ScrapFragment)
            isChangeToDiscover = false
            binding.btmNaviMain.menu.findItem(R.id.menu_main_discover).isChecked = true
            changeFragment(R.id.menu_main_discover)
            Timber.tag("hu").d("fromScrapFragment (true->false): ${isChangeToDiscover}")
        }
    }

    private fun changeFragment(menuItemId: Int) {
        when (menuItemId) {
            R.id.menu_main_drawing -> supportFragmentManager.commit {
                isChangeToStorage = false
                isChangeToDiscover = false
                replace<CourseMainFragment>(R.id.fl_main)
            }
            R.id.menu_main_storage -> supportFragmentManager.commit {
                isChangeToStorage = false
                isChangeToDiscover = false
                replace<StorageMainFragment>(R.id.fl_main)
            }
            R.id.menu_main_discover -> supportFragmentManager.commit {
                isChangeToStorage = false
                isChangeToDiscover = false
                replace<DiscoverFragment>(R.id.fl_main)
            }
            R.id.menu_main_my_page -> supportFragmentManager.commit {
                isChangeToStorage = false
                isChangeToDiscover = false
                replace<MyPageFragment>(R.id.fl_main)
            }
            else -> IllegalArgumentException("${this::class.java.simpleName} Not found menu item id")
        }
    }

    private fun addListener() {
        binding.btmNaviMain.setOnItemSelectedListener {
            changeFragment(it.itemId)
            Timber.tag("hu").d("fromDrawActivity when touch : ${isChangeToStorage}")
            true
        }
    }

    fun getBottomNavMain(): View? {
        return findViewById(R.id.btm_navi_main)
    }

    fun getBtnDeleteCourseMain(): View? {
        return findViewById(R.id.btn_delete_course_main)
    }
}