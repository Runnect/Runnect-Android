package com.runnect.runnect.presentation.mypage.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentMySettingAccountInfoBinding


class MySettingAccountInfoFragment :
    BindingFragment<FragmentMySettingAccountInfoBinding>(R.layout.fragment_my_setting_account_info) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        addListener()
    }

    private fun initLayout() {
        setEmailFromMySetting()
    }

    private fun addListener() {
        binding.ivSettingAccountInfoBack.setOnClickListener {
            moveToMySetting()
        }
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
    }

    private fun moveToMySetting() {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.commit {
            this.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            replace<MySettingFragment>(R.id.fl_main)
        }
    }

    private fun setEmailFromMySetting() {
        val bundle = arguments
        binding.tvSettingAccountInfoIdEmail.text = bundle?.getString(ACCOUNT_INFO_TAG)
    }

    companion object {
        const val ACCOUNT_INFO_TAG = "accountInfo"
    }
}