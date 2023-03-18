package com.runnect.runnect.presentation.storage

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentStorageMainBinding
import com.runnect.runnect.presentation.MainActivity
import timber.log.Timber


class StorageMainFragment :
    BindingFragment<FragmentStorageMainBinding>(R.layout.fragment_storage_main) {


    val viewModel: StorageViewModel by viewModels()

//    private var mainActivity: MainActivity? = null
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mainActivity = context as MainActivity
//    }
//
//    // MainActivity의 메서드를 호출하는 예시 메서드
//    fun callMainActivityMethod() {
//        mainActivity?.hideBtmNavi()
//    }
//
//    private fun hideBtmNavi(){
//        binding.imgBtnHideBtmNavi.setOnClickListener {
//            callMainActivityMethod()
//        }
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = requireActivity()



        initView()
        tabLayoutAction()
//        hideBtmNavi()

    }


    private fun initView() {
        childFragmentManager.commit {
            add<StorageMyDrawFragment>(R.id.fl_main) //여기 add된 게 tabLayoutAction 돌면 메모리 누수 없겠지?
        }
    }

    private fun tabLayoutAction() {
        binding.storageTab.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                // 탭 버튼을 선택할 때 이벤트
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> childFragmentManager.commit {
                            replace<StorageMyDrawFragment>(R.id.fl_main)
                            Timber.tag("hu").d("내가 그린 코스로 이동하였음")
                        }
                        1 -> childFragmentManager.commit {
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


}
