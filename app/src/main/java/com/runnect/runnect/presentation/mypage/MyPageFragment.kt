package com.runnect.runnect.presentation.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentMyPageBinding
import com.runnect.runnect.util.extension.showToast

class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val viewModel: MyPageViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

    }
}