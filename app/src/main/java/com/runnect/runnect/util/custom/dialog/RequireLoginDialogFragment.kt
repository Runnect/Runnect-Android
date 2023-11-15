package com.runnect.runnect.util.custom.dialog

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingDialogFragment
import com.runnect.runnect.databinding.FragmentRequireLoginDialogBinding
import com.runnect.runnect.presentation.login.LoginActivity

class RequireLoginDialogFragment
    : BindingDialogFragment<FragmentRequireLoginDialogBinding>(R.layout.fragment_require_login_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)

        initCancelButtonClickListener()
        initConfirmButtonClickListener()
    }

    private fun initCancelButtonClickListener() {
        binding.btnRequireLoginDialogCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun initConfirmButtonClickListener() {
        binding.btnRequireLoginDialogConfirm.setOnClickListener {
            navigateToLoginScreen()
        }
    }

    private fun navigateToLoginScreen() {
        Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
    }

//    companion object {
//        @JvmStatic
//        fun newInstance() = RequireLoginDialogFragment()
//    }
}
