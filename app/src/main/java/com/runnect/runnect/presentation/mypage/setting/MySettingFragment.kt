package com.runnect.runnect.presentation.mypage.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentMySettingBinding
import com.runnect.runnect.presentation.mypage.MyPageFragment

class MySettingFragment : BindingFragment<FragmentMySettingBinding>(R.layout.fragment_my_setting) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListener()
    }

    private fun addListener() {
        binding.ivSettingBack.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.commit {
                this.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                replace<MyPageFragment>(R.id.fl_main)
            }
        }
    }
}