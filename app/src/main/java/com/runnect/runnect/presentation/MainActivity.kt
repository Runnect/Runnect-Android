package com.runnect.runnect.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.commit
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

    companion object {

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


