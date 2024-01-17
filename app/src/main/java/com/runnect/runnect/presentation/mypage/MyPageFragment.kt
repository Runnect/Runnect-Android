package com.runnect.runnect.presentation.mypage

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.talk.TalkApiClient
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentMyPageBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.mypage.editname.MyPageEditNameActivity
import com.runnect.runnect.presentation.mypage.history.MyHistoryActivity
import com.runnect.runnect.presentation.mypage.reward.MyRewardActivity
import com.runnect.runnect.presentation.mypage.setting.MySettingFragment
import com.runnect.runnect.presentation.mypage.upload.MyUploadActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.extension.getStampResId
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val viewModel: MyPageViewModel by activityViewModels()
    private lateinit var resultEditNameLauncher: ActivityResultLauncher<Intent>
    var isVisitorMode: Boolean = MainActivity.isVisitorMode
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFirebaseAnalytics()
        if (isVisitorMode) {
            activateVisitorMode()
        } else {
            deactivateVisitorMode()
        }

    }

    private fun initFirebaseAnalytics(){
        firebaseAnalytics = Firebase.analytics
    }
    private fun activateVisitorMode() {
        with(binding) {
            ivVisitorMode.isVisible = true
            tvVisitorMode.isVisible = true
            btnVisitorMode.isVisible = true
            constraintInside.isVisible = false

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
            constraintInside.isVisible = true

            binding.vm = viewModel
            binding.lifecycleOwner = this@MyPageFragment.viewLifecycleOwner
            viewModel.getUserInfo()
            addListener()
            addObserver()
            setResultEditNameLauncher()
        }
    }

    private fun setResultEditNameLauncher() {
        resultEditNameLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val name =
                        result.data?.getStringExtra(EXTRA_NICK_NAME) ?: viewModel.nickName.value
                    viewModel.setNickName(name!!)
                }
            }
    }

    private fun addListener() {
        binding.ivMyPageEditFrame.setOnClickListener {
            val intent = Intent(requireContext(), MyPageEditNameActivity::class.java)
            intent.putExtra(EXTRA_NICK_NAME, "${viewModel.nickName.value}")
            val stampResId = requireContext().getStampResId(
                stampId = viewModel.stampId.value,
                resNameParam = RES_NAME,
                resType = RES_STAMP_TYPE,
                packageName = requireContext().packageName
            )
            intent.putExtra(EXTRA_PROFILE, stampResId)
            resultEditNameLauncher.launch(intent)
        }

        binding.viewMyPageMainRewardFrame.setOnClickListener {
            Analytics.logClickedItemEvent(EVENT_TO_REWARD)
            startActivity(Intent(requireContext(), MyRewardActivity::class.java))
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right, R.anim.slide_out_left
            )
        }
        binding.viewMyPageMainHistoryFrame.setOnClickListener {
            Analytics.logClickedItemEvent(EVENT_TO_HISTORY)
            startActivity(Intent(requireContext(), MyHistoryActivity::class.java))
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right, R.anim.slide_out_left
            )
        }

        binding.viewMyPageMainUploadFrame.setOnClickListener {
            Analytics.logClickedItemEvent(EVENT_TO_UPLOADED_COURSE)
            startActivity(Intent(requireContext(), MyUploadActivity::class.java))
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right, R.anim.slide_out_left
            )
        }
        binding.viewMyPageMainSettingFrame.setOnClickListener {
            moveToSettingFragment()
        }
        binding.viewMyPageMainKakaoChannelInquiryFrame.setOnClickListener {
            inquiryKakao()
        }
    }

    private fun moveToSettingFragment() {
        val bundle = Bundle().apply { putString(ACCOUNT_INFO_TAG, viewModel.email.value) }
        requireActivity().supportFragmentManager.commit {
            this.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            replace<MySettingFragment>(R.id.fl_main, args = bundle)
        }
    }

    private fun addObserver() {
        viewModel.nickName.observe(viewLifecycleOwner) { nickName ->
            binding.tvMyPageUserName.text = nickName.toString()
        }

        viewModel.userInfoState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false
                UiState.Loading -> {
                    binding.indeterminateBar.isVisible = true
                    binding.ivMyPageEditFrame.isClickable = false
                    binding.viewMyPageMainSettingFrame.isClickable = false
                }

                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    val stampResId = requireContext().getStampResId(
                        viewModel.stampId.value,
                        RES_NAME,
                        RES_STAMP_TYPE,
                        requireContext().packageName
                    )
                    viewModel.setProfileImg(stampResId)
                    binding.ivMyPageEditFrame.isClickable = true
                    binding.viewMyPageMainSettingFrame.isClickable = true
                }

                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG).d("Failure : ${viewModel.errorMessage.value}")
                }
            }
        }
    }

    private fun inquiryKakao(){
        val url = TalkApiClient.instance.channelChatUrl(BuildConfig.KAKAO_CHANNEL_ID)
        KakaoCustomTabsClient.openWithDefault(requireActivity(), url)
    }
    companion object {
        const val RES_NAME = "mypage_img_stamp_"
        const val RES_STAMP_TYPE = "drawable"
        const val EXTRA_NICK_NAME = "nickname"
        const val EXTRA_PROFILE = "profile_img"
        const val ACCOUNT_INFO_TAG = "accountInfo"

        const val EVENT_TO_HISTORY = "toHistory"
        const val EVENT_TO_REWARD = "toReward"
        const val EVENT_TO_UPLOADED_COURSE = "toUpload"

    }
}