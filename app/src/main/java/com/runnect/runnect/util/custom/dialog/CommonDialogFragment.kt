package com.runnect.runnect.util.custom.dialog

import android.os.Bundle
import android.view.View
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingDialogFragment
import com.runnect.runnect.databinding.FragmentCommonDialogBinding
import com.runnect.runnect.util.extension.getCompatibleParcelableExtra

class CommonDialogFragment
    : BindingDialogFragment<FragmentCommonDialogBinding>(R.layout.fragment_common_dialog) {
    private var onNegativeButtonClicked: () -> Unit = {}
    private var onPositiveButtonClicked: () -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        initDialogText()
        initNegativeButtonClickListener()
        initPositiveButtonClickListener()
    }

    private fun initDialogText() {
        binding.dialogText = arguments?.getCompatibleParcelableExtra(ARG_COMMON_DIALOG)
    }

    private fun initNegativeButtonClickListener() {
        binding.btnCommonDialogNegative.setOnClickListener {
            onNegativeButtonClicked.invoke()
            dismiss()
        }
    }

    private fun initPositiveButtonClickListener() {
        binding.btnCommonDialogPositive.setOnClickListener {
            onPositiveButtonClicked.invoke()
            dismiss()
        }
    }

    companion object {
        private const val ARG_COMMON_DIALOG = "common_dialog_params"

        @JvmStatic
        fun newInstance(
            commonDialogText: CommonDialogText,
            onNegativeButtonClicked: () -> Unit,
            onPositiveButtonClicked: () -> Unit
        ) = CommonDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_COMMON_DIALOG, commonDialogText)
            }
            this.onNegativeButtonClicked = onNegativeButtonClicked
            this.onPositiveButtonClicked = onPositiveButtonClicked
        }
    }
}
