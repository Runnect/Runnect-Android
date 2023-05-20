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
import com.runnect.runnect.util.extension.setCustomDialog
import com.runnect.runnect.util.extension.setDialogClickListener
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_delete.*
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
            logoutDialog.show()
        }

        binding.viewSettingAccountInfoWithdrawalFrame.setOnClickListener {
            withdrawalDialog.show()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,backPressedCallback)
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
            layoutInflater, binding.root, DESC_LOGOUT,
            DESC_LOGOUT_YES, DESC_LOGOUT_NO
        )
    }

    private fun moveToLogin(){
        PreferenceManager.setString(requireContext(), "access", "none")
        PreferenceManager.setString(requireContext(), "refresh", "none")
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setLogoutDialogClickEvent() {
        logoutDialog.setDialogClickListener { which ->
            when (which) {
                logoutDialog.btn_delete_yes -> {
                    moveToLogin()
                }
            }
        }
    }

    private fun initWithdrawalDialog() {
        withdrawalDialog = requireActivity().setCustomDialog(
            layoutInflater, binding.root, DESC_WITHDRAWAL,
            DESC_WITHDRAWAL_YES, DESC_WITHDRAWAL_NO
        )
    }

    private fun setWithdrawalDialogClickEvent() {
        withdrawalDialog.setDialogClickListener { which ->
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
        const val DESC_LOGOUT = "로그아웃 하시겠어요?"
        const val DESC_LOGOUT_YES = "네"
        const val DESC_LOGOUT_NO = "아니오"
        const val DESC_WITHDRAWAL = "정말로 탈퇴하시겟어요?"
        const val DESC_WITHDRAWAL_YES = "네"
        const val DESC_WITHDRAWAL_NO = "아니오"
    }
}