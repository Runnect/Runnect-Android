package com.runnect.runnect.presentation.mypage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.talk.TalkApiClient
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.R
import com.runnect.runnect.presentation.event.VisitorModeManager
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.mypage.editname.MyPageEditNameActivity
import com.runnect.runnect.presentation.mypage.history.MyHistoryActivity
import com.runnect.runnect.presentation.mypage.reward.MyRewardActivity
import com.runnect.runnect.presentation.mypage.setting.MySettingFragment
import com.runnect.runnect.presentation.mypage.upload.MyUploadActivity
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_GOAL_REWARD
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_RUNNING_RECORD
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_UPLOADED_COURSE
import com.runnect.runnect.util.extension.getStampResId
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    @Inject
    lateinit var visitorModeManager: VisitorModeManager

    private val viewModel: MyPageViewModel by activityViewModels()
    private lateinit var resultEditNameLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResultEditNameLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                RunnectTheme {
                    if (visitorModeManager.isVisitorMode) {
                        VisitorModeScreen(
                            onSignUpClick = { navigateToLogin() }
                        )
                    } else {
                        val state by viewModel.state.collectAsState()

                        val stampResId = if (!state.isLoading) {
                            getStampResourceId(state.stampId)
                        } else {
                            R.drawable.user_profile_basic
                        }

                        MyPageScreen(
                            state = state.copy(profileImgResId = stampResId),
                            onEditProfileClick = { navigateToEditName() },
                            onHistoryClick = {
                                Analytics.logClickedItemEvent(EVENT_CLICK_RUNNING_RECORD)
                                navigateTo<MyHistoryActivity>()
                            },
                            onRewardClick = {
                                Analytics.logClickedItemEvent(EVENT_CLICK_GOAL_REWARD)
                                navigateTo<MyRewardActivity>()
                            },
                            onUploadClick = {
                                Analytics.logClickedItemEvent(EVENT_CLICK_UPLOADED_COURSE)
                                navigateTo<MyUploadActivity>()
                            },
                            onSettingClick = { moveToSettingFragment() },
                            onKakaoInquiryClick = { inquiryKakao() }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!visitorModeManager.isVisitorMode) {
            viewModel.intent(MyPageIntent.LoadUserInfo)
        }
    }

    private fun setResultEditNameLauncher() {
        resultEditNameLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val name = result.data?.getStringExtra(EXTRA_NICK_NAME)
                        ?: viewModel.currentState.nickname
                    viewModel.intent(MyPageIntent.UpdateNickname(name))
                }
            }
    }

    private fun navigateToEditName() {
        val intent = Intent(requireContext(), MyPageEditNameActivity::class.java)
        intent.putExtra(EXTRA_NICK_NAME, viewModel.currentState.nickname)
        val stampResId = getStampResourceId(viewModel.currentState.stampId)
        intent.putExtra(EXTRA_PROFILE, stampResId)
        resultEditNameLauncher.launch(intent)
    }

    private fun moveToSettingFragment() {
        val bundle = Bundle().apply { putString(ACCOUNT_INFO_TAG, viewModel.currentState.email) }
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            replace<MySettingFragment>(R.id.fl_main, args = bundle)
        }
    }

    private fun inquiryKakao() {
        val url = TalkApiClient.instance.channelChatUrl(BuildConfig.KAKAO_CHANNEL_ID)
        KakaoCustomTabsClient.openWithDefault(requireActivity(), url)
    }

    private fun navigateToLogin() {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    private fun getStampResourceId(stampId: String): Int {
        return requireContext().getStampResId(
            stampId = stampId,
            resNameParam = RES_NAME,
            resType = RES_STAMP_TYPE,
            packageName = requireContext().packageName
        )
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
