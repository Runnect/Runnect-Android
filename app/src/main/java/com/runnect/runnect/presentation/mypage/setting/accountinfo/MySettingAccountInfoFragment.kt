package com.runnect.runnect.presentation.mypage.setting.accountinfo

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.runnect.runnect.R
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentMySettingAccountInfoBinding
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.mypage.setting.MySettingFragment
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_TRY_LOGOUT
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_TRY_WITHDRAW
import com.runnect.runnect.util.analytics.EventName.EVENT_VIEW_SUCCESS_LOGOUT
import com.runnect.runnect.util.analytics.EventName.EVENT_VIEW_SUCCESS_WITHDRAW
import com.runnect.runnect.util.extension.setCustomDialog
import com.runnect.runnect.util.extension.setDialogButtonClickListener
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_delete.btn_delete_yes
import timber.log.Timber

@AndroidEntryPoint
class MySettingAccountInfoFragment :
    BindingFragment<FragmentMySettingAccountInfoBinding>(R.layout.fragment_my_setting_account_info) {
    private lateinit var logoutDialog: AlertDialog
    private lateinit var withdrawalDialog: AlertDialog
    private val viewModel: MySettingAccountInfoViewModel by viewModels()
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            moveToMySetting()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        addListener()
        addObserver()
        initLogoutDialog()
        setLogoutDialogClickEvent()
        initWithdrawalDialog()
        setWithdrawalDialogClickEvent()
    }

    private fun initLayout() {
        setEmailFromMySetting()
    }

    private fun addListener() {
        binding.ivSettingAccountInfoBack.setOnClickListener {
            moveToMySetting()
        }

        binding.viewSettingAccountInfoLogoutFrame.setOnClickListener {
            Analytics.logClickedItemEvent(EVENT_CLICK_TRY_LOGOUT)
            logoutDialog.show()
        }

        binding.viewSettingAccountInfoWithdrawalFrame.setOnClickListener {
            Analytics.logClickedItemEvent(EVENT_CLICK_TRY_WITHDRAW)
            withdrawalDialog.show()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )
    }

    private fun addObserver() {
        viewModel.withdrawalState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> handleSuccessfulUserDeletion()
                UiState.Failure -> {
                    handleUnsuccessfulUserDeletion()
                }

                else -> {}
            }
        }
    }

    private fun handleSuccessfulUserDeletion() {
        Analytics.logClickedItemEvent(EVENT_VIEW_SUCCESS_WITHDRAW)
        binding.indeterminateBar.isVisible = false
        moveToLogin()
        showToast("탈퇴 처리되었습니다.")
    }

    private fun handleUnsuccessfulUserDeletion() {
        binding.indeterminateBar.isVisible = false
        Timber.tag(ContentValues.TAG).d("Failure : ${viewModel.errorMessage.value}")
    }

    private fun initLogoutDialog() {
        logoutDialog = requireActivity().setCustomDialog(
            layoutInflater, binding.root, DESCRIPTION_LOGOUT,
            DESCRIPTION_LOGOUT_YES, DESCRIPTION_LOGOUT_NO
        )
    }

    private fun moveToLogin() {
        PreferenceManager.setString(requireContext(), TOKEN_KEY_ACCESS, "none")
        PreferenceManager.setString(requireContext(), TOKEN_KEY_REFRESH, "none")
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setLogoutDialogClickEvent() {
        logoutDialog.setDialogButtonClickListener { which ->
            when (which) {
                logoutDialog.btn_delete_yes -> {
                    Analytics.logClickedItemEvent(EVENT_VIEW_SUCCESS_LOGOUT)
                    moveToLogin()
                }
            }
        }
    }

    private fun initWithdrawalDialog() {
        withdrawalDialog = requireActivity().setCustomDialog(
            layoutInflater, binding.root, DESCRIPTION_WITHDRAWAL,
            DESCRIPTION_WITHDRAWAL_YES, DESCRIPTION_WITHDRAWAL_NO
        )
    }

    private fun setWithdrawalDialogClickEvent() {
        withdrawalDialog.setDialogButtonClickListener { which ->
            when (which) {
                withdrawalDialog.btn_delete_yes -> {
                    viewModel.deleteUser()
                }
            }
        }
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
        const val DESCRIPTION_LOGOUT = "로그아웃 하시겠어요?"
        const val DESCRIPTION_LOGOUT_YES = "네"
        const val DESCRIPTION_LOGOUT_NO = "아니오"
        const val DESCRIPTION_WITHDRAWAL = "정말로 탈퇴하시겟어요?"
        const val DESCRIPTION_WITHDRAWAL_YES = "네"
        const val DESCRIPTION_WITHDRAWAL_NO = "아니오"
        const val TOKEN_KEY_ACCESS = "access"
        const val TOKEN_KEY_REFRESH = "refresh"
    }
}