package com.runnect.runnect.presentation

import android.os.Bundle
import androidx.activity.viewModels
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

    var fromDrawActivity: Boolean = false
    var fromScrapFragment: Boolean = false
    var fromEndRunActivity: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.vm = viewModel
        binding.lifecycleOwner = this
        fromValueCheck()
        initView()
        addListener() //이게 있어야 changeFragment를 돌릴 수 있음

    }

    private fun initView() {//MainActivity가 처음 켜질 때 (1. 앱 처음 빌드, 2.다른 액티비티에서 넘어오는 경우)
        if ( (fromDrawActivity == false) && (fromScrapFragment == false) ) {  //1. 앱 처음 빌드
            changeFragment(R.id.menu_main_drawing)
            Timber.tag("hu").d("fromDrawActivity (default) : ${fromDrawActivity}")
        } else if (fromDrawActivity == true){ // 2. 다른 액티비티에서 넘어오는 경우 (from DrawActivity)
            fromDrawActivity = false
            binding.btmNaviMain.menu.findItem(R.id.menu_main_storage).isChecked = true
            changeFragment(R.id.menu_main_storage)
            Timber.tag("hu").d("fromDrawActivity (true->false): ${fromDrawActivity}")
        }
        else if (fromScrapFragment == true){ // 2. 다른 액티비티에서 넘어오는 경우 (from ScrapFragment)
            fromScrapFragment = false //이게 어디서 날라오는 거지? -> StorageFragment
            binding.btmNaviMain.menu.findItem(R.id.menu_main_discover).isChecked = true
            changeFragment(R.id.menu_main_discover)
            Timber.tag("hu").d("fromScrapFragment (true->false): ${fromScrapFragment}")
        }
    }


    private fun fromValueCheck(){

        fromDrawActivity =
            intent.getBooleanExtra("fromDrawActivity", false) //null 대신 default value를 false로 설정함.
        Timber.tag("hu")
            .d("Is this from DrawActivity? : ${fromDrawActivity}")

        fromScrapFragment =
            intent.getBooleanExtra("fromScrapFragment", false) //null 대신 default value를 false로 설정함.
        Timber.tag("hu")
            .d("Is this from ScrapFragment? : ${fromScrapFragment}")

        //지금 변수명이 헷갈리게 돼있음 fromDrawActivity가 true면 storage로 이동하게 돼있어서 이것보다는 isAvailableToStorage, toStorage 이런 걸로 바꾸는 게 나을 듯
        fromEndRunActivity = intent.getStringExtra("dataFrom")
        if(fromEndRunActivity == "myDraw"){
            fromDrawActivity = true //보관함으로 갈 수 있게
        }

        if(fromEndRunActivity == "draw"){
            //아무것도 안 해도 됨. (fromDrawActivity == false) && (fromScrapFragment == false)이기만하면 courseMain을 띄우니까
        }

    }

    private fun addListener() {
        binding.btmNaviMain.setOnItemSelectedListener {

            changeFragment(it.itemId)
            Timber.tag("hu").d("fromDrawActivity when touch : ${fromDrawActivity}")
            true
        }
    }

    private fun changeFragment(menuItemId: Int) {

        when (menuItemId) {
            R.id.menu_main_drawing -> supportFragmentManager.commit {
                fromDrawActivity = false
                fromScrapFragment = false
                replace<CourseMainFragment>(R.id.fl_main) //replace면 back stack에 안 쌓이는 건가?
            }
            R.id.menu_main_storage -> supportFragmentManager.commit {
                fromDrawActivity = false
                fromScrapFragment = false
                replace<StorageMainFragment>(R.id.fl_main)
            }
            R.id.menu_main_discover -> supportFragmentManager.commit {
                fromDrawActivity = false
                fromScrapFragment = false
                replace<DiscoverFragment>(R.id.fl_main)

            }
            R.id.menu_main_my_page -> supportFragmentManager.commit {
                fromDrawActivity = false
                fromScrapFragment = false
                replace<MyPageFragment>(R.id.fl_main)

            }

            else -> IllegalArgumentException("${this::class.java.simpleName} Not found menu item id")
        }
    }
}