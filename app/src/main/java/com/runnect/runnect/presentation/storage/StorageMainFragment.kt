package com.runnect.runnect.presentation.storage

import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.runnect.runnect.R
import com.runnect.runnect.binding.BaseVisitorFragment
import com.runnect.runnect.databinding.FragmentStorageMainBinding
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_MY_DRAW_STORAGE_COURSE_DRAWING_START
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_SCRAP_COURSE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StorageMainFragment :
    BaseVisitorFragment<FragmentStorageMainBinding>(R.layout.fragment_storage_main) {
    val viewModel: StorageViewModel by viewModels()
    
    override val visitorContainer by lazy { binding.clVisitorMode }
    override val contentViews by lazy { listOf(binding.storageTab, binding.tabUnderLine) }

    override fun onContentModeInit() {
        binding.lifecycleOwner = requireActivity()
        initView()
        tabLayoutAction()
    }

    private fun initView() {
        childFragmentManager.commit {
            add<StorageMyDrawFragment>(R.id.fl_main)
        }
    }

    private fun tabLayoutAction() {
        binding.storageTab.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> childFragmentManager.commit {
                            Analytics.logClickedItemEvent(
                                EVENT_CLICK_MY_DRAW_STORAGE_COURSE_DRAWING_START
                            )
                            replace<StorageMyDrawFragment>(R.id.fl_main)
                        }

                        1 -> childFragmentManager.commit {
                            Analytics.logClickedItemEvent(EVENT_CLICK_SCRAP_COURSE)
                            replace<StorageScrapFragment>(R.id.fl_main)
                        }

                        else -> childFragmentManager.commit {
                            replace<StorageMyDrawFragment>(R.id.fl_main)
                        }
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
}
