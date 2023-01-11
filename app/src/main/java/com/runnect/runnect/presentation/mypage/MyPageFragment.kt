package com.runnect.runnect.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentMyPageBinding
import com.runnect.runnect.presentation.mypage.history.MyHistoryActivity
import com.runnect.runnect.presentation.mypage.reward.MyRewardActivity
import com.runnect.runnect.presentation.mypage.upload.MyUploadActivity
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val viewModel: MyPageViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        viewModel.getUserInfo()
        addListener()
        addObserver()

    }

    private fun addListener() {
        binding.ivMyPageEditFrame.setOnClickListener {
            val dialog = MyPageEditNameDialog()
            dialog.show(activity?.supportFragmentManager!!, "MyPageEditNameDialog")
        }
        binding.viewMyPageMainRewardFrame.setOnClickListener {
            startActivity(Intent(requireContext(), MyRewardActivity::class.java))
        }
        binding.viewMyPageMainHistoryFrame.setOnClickListener {
            startActivity(Intent(requireContext(), MyHistoryActivity::class.java))
        }
        binding.viewMyPageMainUploadFrame.setOnClickListener {
            startActivity(Intent(requireContext(), MyUploadActivity::class.java))
        }
    }

    private fun addObserver() {
        viewModel.nickName.observe(viewLifecycleOwner) { nickName ->
            binding.tvMyPageUserName.text = nickName.toString()
        }
    }

}