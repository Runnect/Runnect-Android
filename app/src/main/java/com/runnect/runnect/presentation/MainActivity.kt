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

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.vm = viewModel
        binding.lifecycleOwner = this
        addListener()
        changeFragment(R.id.menu_main_drawing)
    }

    private fun addListener() {
        binding.btmNaviMain.setOnItemSelectedListener {
            changeFragment(it.itemId)
            true
        }
    }

    private fun changeFragment(menuItemId: Int) {
        when (menuItemId) {
            R.id.menu_main_drawing -> supportFragmentManager.commit {
                replace<CourseMainFragment>(R.id.fl_main)
            }
            R.id.menu_main_storage -> supportFragmentManager.commit {
                replace<StorageFragment>(R.id.fl_main)
            }
            R.id.menu_main_discover -> supportFragmentManager.commit {
                replace<DiscoverFragment>(R.id.fl_main)
            }
            R.id.menu_main_my_page -> supportFragmentManager.commit {
                replace<MyPageFragment>(R.id.fl_main)
            }

            else -> IllegalArgumentException("${this::class.java.simpleName} Not found menu item id")
        }
    }
}