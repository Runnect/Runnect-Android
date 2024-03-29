package com.runnect.runnect.presentation.mypage.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentMySettingBinding
import com.runnect.runnect.presentation.mypage.MyPageFragment
import com.runnect.runnect.presentation.mypage.setting.accountinfo.MySettingAccountInfoFragment
import com.runnect.runnect.util.extension.showWebBrowser

class MySettingFragment : BindingFragment<FragmentMySettingBinding>(R.layout.fragment_my_setting) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListener()
        setVisibleDeveloperMode()
        registerBackPressedCallback()
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToMyPageFragment()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun addListener() {
        binding.ivSettingBack.setOnClickListener {
            navigateToMyPageFragment()
        }
        binding.viewSettingAccountInfoFrame.setOnClickListener {
            moveToMySettingAccountInfo()
        }
        binding.viewSettingReportFrame.setOnClickListener {
            requireContext().showWebBrowser(REPORT_URL)
        }
        binding.viewSettingTermsFrame.setOnClickListener {
            requireContext().showWebBrowser(TERMS_URL)
        }

        binding.constSettingDeveloperModeFrame.setOnClickListener {
            moveToDevMode()
        }
    }

    private fun setVisibleDeveloperMode() {
        binding.constSettingDeveloperModeFrame.isVisible = BuildConfig.DEBUG
    }

    private fun navigateToMyPageFragment() {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            replace<MyPageFragment>(R.id.fl_main)
        }
    }

    private fun moveToMySettingAccountInfo() {
        val emailFromMyPage = getEmailFromMyPage()
        val bundle = Bundle().apply { putString(ACCOUNT_INFO_TAG, emailFromMyPage) }
        parentFragmentManager.commit {
            this.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            replace<MySettingAccountInfoFragment>(R.id.fl_main, args = bundle)
        }
    }

    private fun moveToDevMode() {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(DEV_MODE_SCHEME)
        }.let(this::startActivity)
    }

    private fun getEmailFromMyPage(): String? {
        val bundleFromMyPage = arguments
        return bundleFromMyPage?.getString(ACCOUNT_INFO_TAG)
    }

    companion object {
        const val ACCOUNT_INFO_TAG = "accountInfo"
        const val REPORT_URL =
            "https://docs.google.com/forms/d/e/1FAIpQLSek2rkClKfGaz1zwTEHX3Oojbq_pbF3ifPYMYezBU0_pe-_Tg/viewform"
        const val TERMS_URL =
            "https://third-sight-046.notion.site/Runnect-5dfee19ccff04c388590e5ee335e77ed"
        private const val DEV_MODE_SCHEME = "runnect://devmode"

    }
}
