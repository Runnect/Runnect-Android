package com.runnect.runnect.presentation.mypage

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.talk.TalkApiClient
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentMyPageBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.mypage.dto.UserDto
import com.runnect.runnect.presentation.mypage.editname.MyPageEditNameActivity
import com.runnect.runnect.presentation.mypage.history.MyHistoryActivity
import com.runnect.runnect.presentation.mypage.reward.MyRewardActivity
import com.runnect.runnect.presentation.mypage.setting.MySettingFragment
import com.runnect.runnect.presentation.mypage.upload.MyUploadActivity
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_GOAL_REWARD
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_RUNNING_RECORD
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_UPLOADED_COURSE
import com.runnect.runnect.util.extension.applyScreenEnterAnimation
import com.runnect.runnect.util.extension.getStampResId
import com.runnect.runnect.util.extension.repeatOnStarted
import com.runnect.runnect.util.extension.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {

    private val viewModel: MyPageViewModel by activityViewModels()

    private val userData: UserDto
        get() = viewModel.userData.value

    private val stampResId: Int
        get() = activity?.run {
            getStampResId(
                stampId = userData.stampId,
                resNameParam = RES_NAME,
                resType = RES_STAMP_TYPE,
                packageName = packageName
            )
        } ?: 0

    private lateinit var resultEditNameLauncher: ActivityResultLauncher<Intent>
    private var isVisitorMode: Boolean = MainActivity.isVisitorMode

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isVisitorMode) {
            activateVisitorMode()
        } else {
            deactivateVisitorMode()
        }
    }

    private fun activateVisitorMode() {
        with(binding) {
            setVisitorMode(true)

            btnVisitorMode.setOnClickListener {
                activity?.let {
                    Intent(it, LoginActivity::class.java).let(::startActivity)
                    it.finish()
                }
            }
        }
    }

    private fun deactivateVisitorMode() {
        setVisitorMode(false)
        addListener()
        addObserver()
        setResultEditNameLauncher()

        viewModel.getUserInfo()
        with(binding) {
            vm = viewModel
            lifecycleOwner = this@MyPageFragment.viewLifecycleOwner
        }
    }

    private fun setResultEditNameLauncher() {
        resultEditNameLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val name = result.data?.getStringExtra(EXTRA_NICK_NAME) ?: ""
                    viewModel.updateUser(
                        userData.copy(nickName = name)
                    )
                }
            }
    }

    private fun addListener() {
        binding.ivMyPageEditFrame.setOnClickListener {
            Intent(requireContext(), MyPageEditNameActivity::class.java).apply {
                putExtra(EXTRA_NICK_NAME, userData.nickName)
                putExtra(EXTRA_PROFILE, stampResId)
            }.let(resultEditNameLauncher::launch)
        }

        binding.viewMyPageMainRewardFrame.setOnClickListener {
            Analytics.logClickedItemEvent(EVENT_CLICK_GOAL_REWARD)
            startActivityWithAnimation(MyRewardActivity::class.java)
        }

        binding.viewMyPageMainHistoryFrame.setOnClickListener {
            Analytics.logClickedItemEvent(EVENT_CLICK_RUNNING_RECORD)
            startActivityWithAnimation(MyHistoryActivity::class.java)
        }

        binding.viewMyPageMainUploadFrame.setOnClickListener {
            Analytics.logClickedItemEvent(EVENT_CLICK_UPLOADED_COURSE)
            startActivityWithAnimation(MyUploadActivity::class.java)
        }

        binding.viewMyPageMainSettingFrame.setOnClickListener {
            moveToSettingFragment()
        }

        binding.viewMyPageMainKakaoChannelInquiryFrame.setOnClickListener {
            inquiryKakao()
        }
    }

    private fun moveToSettingFragment() {
        val bundle = Bundle().apply {
            putString(ACCOUNT_INFO_TAG, userData.email)
        }

        requireActivity().supportFragmentManager.commit {
            this.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            replace<MySettingFragment>(R.id.fl_main, args = bundle)
        }
    }

    private fun addObserver() {
        repeatOnStarted(
            {
                viewModel.userState.collect {
                    when (it) {
                        is MyPageViewModel.UserState.Loading -> handleLoading(true)
                        is MyPageViewModel.UserState.Success -> handleSuccess()
                        is MyPageViewModel.UserState.Failure -> handleFailure()
                    }
                }
            },
            {
                viewModel.eventState.collect {
                    when (it) {
                        is BaseViewModel.EventState.ShowSnackBar -> {
                            context?.showSnackbar(binding.root, it.message)
                        }
                        is BaseViewModel.EventState.NetworkError -> {
                            context?.showSnackbar(binding.root, it.message)
                        }
                        else -> Unit
                    }
                }
            }
        )
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            indeterminateBar.isVisible = isLoading
            ivMyPageEditFrame.isClickable = !isLoading
            viewMyPageMainSettingFrame.isClickable = !isLoading
        }
    }

    private fun handleSuccess() {
        handleLoading(false)
        viewModel.updateUser(
            userData.copy(profileImgResId = stampResId)
        )
        binding. tvMyPageUserName.text = userData.nickName
    }

    private fun handleFailure() {
        binding.indeterminateBar.isVisible = false
    }

    private fun setVisitorMode(isVisitor: Boolean) {
        with(binding) {
            ivVisitorMode.isVisible = isVisitor
            tvVisitorMode.isVisible = isVisitor
            btnVisitorMode.isVisible = isVisitor
            constraintInside.isVisible = !isVisitor
        }
    }

    private fun startActivityWithAnimation(activityClass: Class<*>) {
        activity?.let {
            Intent(it, activityClass).let(::startActivity)
            it.applyScreenEnterAnimation()
        }
    }

    private fun inquiryKakao() {
        val url = TalkApiClient.instance.channelChatUrl(BuildConfig.KAKAO_CHANNEL_ID)
        KakaoCustomTabsClient.openWithDefault(requireActivity(), url)
    }

    companion object {
        const val RES_NAME = "mypage_img_stamp_"
        const val RES_STAMP_TYPE = "drawable"
        const val EXTRA_NICK_NAME = "nickname"
        const val EXTRA_PROFILE = "profile_img"
        const val ACCOUNT_INFO_TAG = "accountInfo"
    }
}