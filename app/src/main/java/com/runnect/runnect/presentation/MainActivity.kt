package com.runnect.runnect.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
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
import com.runnect.runnect.presentation.storage.StorageScrapFragment
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

//    var backStackCount: Int = 0

    companion object {
        var isVisitorMode = false
        var discoverFragment: DiscoverFragment? = null
        var storageScrapFragment: StorageScrapFragment? = null
//        const val MAX_BACKSTACK_SIZE = 30

        fun updateDiscoverFragment() {
            discoverFragment?.getRecommendCourses()
        }

        fun updateStorageScrap() {
            storageScrapFragment?.getCourse()
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
//        backStackCount = supportFragmentManager.backStackEntryCount

        when (menuItemId) {
            R.id.menu_main_drawing -> supportFragmentManager.commit {
//                clearBackStackIfFull()

                isChangeToStorage = false
                isChangeToDiscover = false
                replace<CourseMainFragment>(R.id.fl_main)
            }
            R.id.menu_main_storage -> supportFragmentManager.commit {
//                clearBackStackIfFull()

                isChangeToStorage = false
                isChangeToDiscover = false
                replace<StorageMainFragment>(R.id.fl_main)
            }
            R.id.menu_main_discover -> supportFragmentManager.commit {
//                clearBackStackIfFull()

                isChangeToStorage = false
                isChangeToDiscover = false
                replace<DiscoverFragment>(R.id.fl_main)
            }
            R.id.menu_main_my_page -> supportFragmentManager.commit {
//                clearBackStackIfFull()

                isChangeToStorage = false
                isChangeToDiscover = false
                addToBackStack(null) // 이전 프래그먼트를 백 스택에 추가하지 않음
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

//    private fun clearBackStackIfFull() {
//        if (backStackCount >= MAX_BACKSTACK_SIZE) {
//            supportFragmentManager.popBackStackImmediate(
//                null,
//                FragmentManager.POP_BACK_STACK_INCLUSIVE
//            )
//        }
//    }

    fun getBottomNavMain(): View? {
        return findViewById(R.id.btm_navi_main)
    } //R.id로 찾아오는 거면 굳이 여기서 함수로 호출하지 않고 StorageMyDrawFragment에서 바로 접근해도 되겠는데?
    //그러면 MainActivity를 참조 안 해도 되니까 StorageMyDrawFragment에 작성된 참조 코드도 제거해줄 수 있을 듯.

    fun getBtnDeleteCourseMain(): View? {
        return findViewById(R.id.btn_delete_course_main)
    }
}