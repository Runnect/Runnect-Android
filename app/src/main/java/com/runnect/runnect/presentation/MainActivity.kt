package com.runnect.runnect.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.add
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


//    = intent.getBooleanExtra("fromDrawActivity", false) //defaultValue를 써주면 null 대신 저게 써지는 건가 그러면 좋겠는데

    lateinit var fromDrawActivity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.vm = viewModel
        binding.lifecycleOwner = this
        fromDrawActivity()
        initView()
        addListener() //이게 있어야 changeFragment를 돌릴 수 있는 거

    }

    private fun initView() {
        if (fromDrawActivity == "null") {
            changeFragment(R.id.menu_main_drawing)
            Timber.tag("hu").d("fromDrawActivity 로그1 : ${fromDrawActivity}")
        } else {
            changeFragment(R.id.menu_main_storage)
            Timber.tag("hu").d("fromDrawActivity 로그2 : ${fromDrawActivity}")
        }
    }

    private fun fromDrawActivity() { //지금 로그 1,2가 true로 잘 찍히는데 RunTimeError 뜨면서 죽음. 내 생각엔 changeFragment로 main_drawing을 띄우기 전에 fromDrawActivity()가 돌면서 fragment 전환을 일으키기 때문에 죽는듯
        //맨 처음 앱 들어올 땐 로그 1,2가 true로 안 넘어가니까 전환할 필요가 없어서 안 죽는 거고
        fromDrawActivity = intent.getStringExtra("fromDrawActivity").toString()
        Timber.tag("hu")
            .d("fromDrawActivity 로그3 : ${fromDrawActivity}") //위에 toString 때문에 로그엔 null이라 찍히지만 이게 String인거야.


    }

    private fun addListener() {
        binding.btmNaviMain.setOnItemSelectedListener {

            changeFragment(it.itemId)
            fromDrawActivity =
                "null" // 근데 사실 이 코드는 필요 없는듯? 왜냐하면 null check는 처음 화면 킬 때만 하는 거고 그 후엔 그냥 조건 없이 addListener가 도는 거라서.
            Timber.tag("hu").d("fromDrawActivity 로그4 : ${fromDrawActivity}")
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