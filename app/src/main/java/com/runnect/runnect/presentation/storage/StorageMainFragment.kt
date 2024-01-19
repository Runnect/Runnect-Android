package com.runnect.runnect.presentation.storage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentStorageMainBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.mypage.upload.MyUploadActivity
import com.runnect.runnect.util.analytics.Analytics
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StorageMainFragment :
    BindingFragment<FragmentStorageMainBinding>(R.layout.fragment_storage_main) {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    val viewModel: StorageViewModel by viewModels()
    var isVisitorMode: Boolean = MainActivity.isVisitorMode

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFirebaseAnalytics()
        if (isVisitorMode) {
            activateVisitorMode()
        } else {
            deactivateVisitorMode()
        }
    }

    private fun activateVisitorMode() {
        with(binding) {
            ivVisitorMode.isVisible = true
            tvVisitorMode.isVisible = true
            btnVisitorMode.isVisible = true
            storageTab.isVisible = false
            tabUnderLine.isVisible = false

            btnVisitorMode.setOnClickListener {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun deactivateVisitorMode() {
        with(binding) {
            ivVisitorMode.isVisible = false
            tvVisitorMode.isVisible = false
            btnVisitorMode.isVisible = false
            storageTab.isVisible = true
            tabUnderLine.isVisible = true

            binding.lifecycleOwner = requireActivity()
            initView()
            tabLayoutAction()
        }
    }

    private fun initView() {
        childFragmentManager.commit {
            add<StorageMyDrawFragment>(R.id.fl_main)
        }
    }

    private fun initFirebaseAnalytics() {
        firebaseAnalytics = Firebase.analytics
    }

    private fun tabLayoutAction() {
        binding.storageTab.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> childFragmentManager.commit {
                            Analytics.logClickedItemEvent(EVENT_CLICK_MY_DRAW_STORAGE_COURSE_DRAWING_START)
                            replace<StorageMyDrawFragment>(R.id.fl_main)
                            Timber.tag("hu").d("내가 그린 코스로 이동하였음")
                        }

                        1 -> childFragmentManager.commit {
                            Analytics.logClickedItemEvent(EVENT_CLICK_SCRAP_COURSE)
                            replace<StorageScrapFragment>(R.id.fl_main)
                            Timber.tag("hu").d("스크랩으로 이동하였음")
                        }

                        else -> IllegalArgumentException("${this::class.java.simpleName} Not found menu item id")
                    }
                }

                // 다른 탭 버튼을 눌러 선택된 탭 버튼이 해제될 때 이벤트
                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                // 선택된 탭 버튼을 다시 선택할 때 이벤트
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            }
        )
    }
    companion object {
        const val EVENT_CLICK_MY_DRAW_STORAGE_COURSE_DRAWING_START= "click_my_storage_course_drawing_start"
        const val EVENT_CLICK_SCRAP_COURSE = "click_scrap_course"
    }

}
