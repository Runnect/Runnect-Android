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
import com.runnect.runnect.presentation.storage.StorageFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()

    var fromDrawActivity: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.vm = viewModel
        binding.lifecycleOwner = this
        fromDrawActivity()
        initView()
        addListener() //이게 있어야 changeFragment를 돌릴 수 있는 거

    }

    private fun initView() {
        if (fromDrawActivity == false) {
            changeFragment(R.id.menu_main_drawing)
            Timber.tag("hu").d("fromDrawActivity (default) : ${fromDrawActivity}")
        } else {
            fromDrawActivity = false
            binding.btmNaviMain.menu.findItem(R.id.menu_main_storage).isChecked = true
            changeFragment(R.id.menu_main_storage)
            Timber.tag("hu").d("fromDrawActivity (true->false): ${fromDrawActivity}")
        }
    }

    private fun fromDrawActivity() {
        fromDrawActivity = intent.getBooleanExtra("fromDrawActivity", false) //null 대신 default value를 false로 설정함.
        Timber.tag("hu")
            .d("Is this from DrawActivity? : ${fromDrawActivity}")


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
                replace<CourseMainFragment>(R.id.fl_main)
            }
            R.id.menu_main_storage -> supportFragmentManager.commit {
                fromDrawActivity = false
                replace<StorageFragment>(R.id.fl_main)
            }
            R.id.menu_main_discover -> supportFragmentManager.commit {
                fromDrawActivity = false
                replace<DiscoverFragment>(R.id.fl_main)

            }
            R.id.menu_main_my_page -> supportFragmentManager.commit {
                fromDrawActivity = false
                replace<MyPageFragment>(R.id.fl_main)

            }

            else -> IllegalArgumentException("${this::class.java.simpleName} Not found menu item id")
        }
    }
}