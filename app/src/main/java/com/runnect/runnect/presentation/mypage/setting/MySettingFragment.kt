package com.runnect.runnect.presentation.mypage.setting

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentMySettingBinding
import com.runnect.runnect.presentation.mypage.MyPageFragment
import com.runnect.runnect.presentation.mypage.setting.accountinfo.MySettingAccountInfoFragment
import com.runnect.runnect.util.extension.startWebView

class MySettingFragment : BindingFragment<FragmentMySettingBinding>(R.layout.fragment_my_setting) {
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            moveToMyPage()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListener()
    }

    private fun addListener() {
        binding.ivSettingBack.setOnClickListener {
            moveToMyPage()
        }
        binding.viewSettingAccountInfoFrame.setOnClickListener {
            moveToMySettingAccountInfo()
        }
        binding.viewSettingReportFrame.setOnClickListener {
            requireContext().startWebView(REPORT_URL)
        }
        binding.viewSettingTermsFrame.setOnClickListener {
            requireContext().startWebView(TERMS_URL)
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )
    }

    private fun moveToMyPage() {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.commit {
            this.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            replace<MyPageFragment>(R.id.fl_main)
        }
    }

    private fun moveToMySettingAccountInfo() {
        val emailFromMyPage = getEmailFromMyPage()
        val bundle = Bundle().apply { putString(ACCOUNT_INFO_TAG, emailFromMyPage) }
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.commit {
            this.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            replace<MySettingAccountInfoFragment>(R.id.fl_main, args = bundle)
        }
    }

    private fun getEmailFromMyPage(): String? {
        val bundleFromMyPage = arguments
        return bundleFromMyPage?.getString(ACCOUNT_INFO_TAG)
    }

    companion object {
        const val ACCOUNT_INFO_TAG = "accountInfo"
        const val REPORT_URL =
            "https://docs.google.com/forms/d/e/1FAIpQLSek2rkClKfGaz1zwTEHX3Oojbq_pbF3ifPYMYezBU0_pe-_Tg/viewform"
        const val TERMS_URL = "https://www.notion.so/Runnect-5dfee19ccff04c388590e5ee335e77ed?pvs=4"
    }
}
