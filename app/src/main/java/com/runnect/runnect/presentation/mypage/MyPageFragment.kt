package com.runnect.runnect.presentation.mypage

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import coil3.load
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.talk.TalkApiClient
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.R
import com.runnect.runnect.binding.BaseVisitorFragment
import com.runnect.runnect.databinding.FragmentMyPageBinding
import com.runnect.runnect.presentation.mypage.editname.MyPageEditNameActivity
import com.runnect.runnect.presentation.mypage.history.MyHistoryActivity
import com.runnect.runnect.presentation.mypage.reward.MyRewardActivity
import com.runnect.runnect.presentation.mypage.setting.MySettingFragment
import com.runnect.runnect.presentation.mypage.upload.MyUploadActivity
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_GOAL_REWARD
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_RUNNING_RECORD
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_UPLOADED_COURSE
import com.runnect.runnect.util.extension.getStampResId
import com.runnect.runnect.util.extension.repeatOnStarted
import com.runnect.runnect.util.extension.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MyPageFragment : BaseVisitorFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val viewModel: MyPageViewModel by activityViewModels()
    private lateinit var resultEditNameLauncher: ActivityResultLauncher<Intent>

    override val visitorContainer by lazy { binding.clVisitorMode }
    override val contentViews by lazy { listOf(binding.constraintInside) }

    override fun onContentModeInit() {
        binding.lifecycleOwner = this@MyPageFragment.viewLifecycleOwner
        viewModel.intent(MyPageIntent.LoadUserInfo)
        addListener()
        addObserver()
        setResultEditNameLauncher()
    }

    private fun setResultEditNameLauncher() {
        resultEditNameLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val name = result.data?.getStringExtra(EXTRA_NICK_NAME)
                        ?: viewModel.currentState.nickname
                    viewModel.intent(MyPageIntent.UpdateNickname(name))
                }
            }
    }

    private fun addListener() {
        with(binding) {
            ivMyPageEditFrame.setOnClickListener {
                val intent = Intent(requireContext(), MyPageEditNameActivity::class.java)
                intent.putExtra(EXTRA_NICK_NAME, viewModel.currentState.nickname)
                val stampResId = getStampResourceId()
                intent.putExtra(EXTRA_PROFILE, stampResId)
                resultEditNameLauncher.launch(intent)
            }

            viewMyPageMainRewardFrame.setOnClickListener {
                Analytics.logClickedItemEvent(EVENT_CLICK_GOAL_REWARD)
                navigateTo<MyRewardActivity>()
            }
            viewMyPageMainHistoryFrame.setOnClickListener {
                Analytics.logClickedItemEvent(EVENT_CLICK_RUNNING_RECORD)
                navigateTo<MyHistoryActivity>()
            }

            viewMyPageMainUploadFrame.setOnClickListener {
                Analytics.logClickedItemEvent(EVENT_CLICK_UPLOADED_COURSE)
                navigateTo<MyUploadActivity>()
            }
            viewMyPageMainSettingFrame.setOnClickListener {
                moveToSettingFragment()
            }
            viewMyPageMainKakaoChannelInquiryFrame.setOnClickListener {
                inquiryKakao()
            }
        }
    }

    private fun moveToSettingFragment() {
        val bundle = Bundle().apply { putString(ACCOUNT_INFO_TAG, viewModel.currentState.email) }
        requireActivity().supportFragmentManager.commit {
            this.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            replace<MySettingFragment>(R.id.fl_main, args = bundle)
        }
    }

    private fun addObserver() {
        repeatOnStarted {
            viewModel.state.collectLatest { state ->
                bindState(state)
            }
        }
    }

    private fun bindState(state: MyPageUiState) {
        setLoadingState(state.isLoading)

        if (!state.isLoading && state.error == null) {
            with(binding) {
                tvMyPageUserName.text = state.nickname
                tvMyPageUserLv.text = state.level
                pbMyPageProgress.progress = state.levelPercent
                tvMyPageProgressCurrent.text = state.levelPercent.toString()
                ivMyPageProfile.load(state.profileImgResId)
            }

            val stampResId = getStampResourceId()
            viewModel.intent(MyPageIntent.UpdateProfileImg(stampResId))
        }

        state.error?.let {
            context?.showSnackbar(anchorView = binding.root, message = it)
        }
    }

    private fun inquiryKakao() {
        val url = TalkApiClient.instance.channelChatUrl(BuildConfig.KAKAO_CHANNEL_ID)
        KakaoCustomTabsClient.openWithDefault(requireActivity(), url)
    }

    private fun getStampResourceId(): Int {
        return requireContext().getStampResId(
            stampId = viewModel.currentState.stampId,
            resNameParam = RES_NAME,
            resType = RES_STAMP_TYPE,
            packageName = requireContext().packageName
        )
    }

    private fun setLoadingState(isLoading: Boolean) {
        with(binding) {
            indeterminateBar.isVisible = isLoading
            ivMyPageEditFrame.isClickable = !isLoading
            viewMyPageMainSettingFrame.isClickable = !isLoading
        }
    }

    private inline fun <reified T : Activity> navigateTo() {
        startActivity(Intent(requireContext(), T::class.java))
        requireActivity().overridePendingTransition(
            R.anim.slide_in_right, R.anim.slide_out_left
        )
    }

    companion object {
        const val RES_NAME = "mypage_img_stamp_"
        const val RES_STAMP_TYPE = "drawable"
        const val EXTRA_NICK_NAME = "nickname"
        const val EXTRA_PROFILE = "profile_img"
        const val ACCOUNT_INFO_TAG = "accountInfo"
    }
}
