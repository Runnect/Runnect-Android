package com.runnect.runnect.presentation

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.runnect.runnect.R
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.vm = viewModel
        binding.lifecycleOwner = this
        CheckIntentValue()
        initView()
        addListener() //이게 있어야 changeFragment를 돌릴 수 있음

    }

    fun getBtmNaviMain(): View? {
        return findViewById(R.id.btm_navi_main)
    }

    fun getBtnDeleteCourseMain(): View? {
        return findViewById(R.id.btn_delete_course_main)
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


    private fun CheckIntentValue() {
        isChangeToStorage =
            intent.getBooleanExtra("fromDrawActivity", false)

        isChangeToDiscover =
            intent.getBooleanExtra("fromScrapFragment", false)

        fromEndRunActivity = intent.getStringExtra("dataFrom")
        if (fromEndRunActivity == "myDraw") {
            isChangeToStorage = true
        }

        if (fromEndRunActivity == "draw") {
            //아무것도 안 해도 됨. (fromDrawActivity == false) && (fromScrapFragment == false)이기만하면 courseMain을 띄우니까
        }
    }

    private fun addListener() {
        binding.btmNaviMain.setOnItemSelectedListener {
            changeFragment(it.itemId)
            Timber.tag("hu").d("fromDrawActivity when touch : ${isChangeToStorage}")
            true
        }
    }

    private fun changeFragment(menuItemId: Int) {

        when (menuItemId) {
            R.id.menu_main_drawing -> supportFragmentManager.commit {
                isChangeToStorage = false
                isChangeToDiscover = false
                replace<CourseMainFragment>(R.id.fl_main) //replace면 back stack에 안 쌓이는 건가?
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
}