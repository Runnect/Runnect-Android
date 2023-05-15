package com.runnect.runnect.presentation.mypage.setting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.runnect.runnect.R
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentMySettingAccountInfoBinding
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.util.extension.setCustomDialog
import com.runnect.runnect.util.extension.setDialogClickListener
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_delete.*

@AndroidEntryPoint
class MySettingAccountInfoFragment :
    BindingFragment<FragmentMySettingAccountInfoBinding>(R.layout.fragment_my_setting_account_info) {
    private lateinit var logoutDialog: AlertDialog
    private val viewModel: MySettingAccountInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        addListener()
        initLogoutDialog()
        setLogoutDialogClickEvent()
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
    }

    private fun initLogoutDialog() {
        logoutDialog = requireActivity().setCustomDialog(
            layoutInflater, binding.root, DESC_LOGOUT,
            DESC_LOGOUT_YES, DESC_LOGOUT_NO
        )
    }

    private fun setLogoutDialogClickEvent() {
        logoutDialog.setDialogClickListener { which ->
            when (which) {
                logoutDialog.btn_delete_yes -> {
                    PreferenceManager.setString(requireContext(), "access","none")
                    PreferenceManager.setString(requireContext(), "refresh","none")
                    val intent = Intent(requireActivity(),LoginActivity::class.java)
                    intent.putExtra("isLogout",true)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                    startActivity(intent)
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
    }
}