package com.runnect.runnect.util.custom

import android.os.Bundle
import android.view.View
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingDialogFragment
import com.runnect.runnect.databinding.FragmentCommonDialogBinding

class CommonDialogFragment(
    private val description: String,
    private val negativeButtonText: String,
    private val positiveButtonText: String,
    val onNegativeButtonClicked: () -> Unit,
    val onPositiveButtonClicked: () -> Unit
) : BindingDialogFragment<FragmentCommonDialogBinding>(R.layout.fragment_common_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        initDialogText()
        initNegativeButtonClickListener()
        initPositiveButtonClickListener()
    }

    private fun initDialogText() {
        with(binding){
            tvCommonDialogDesc.text = description
            btnCommonDialogNegative.text = negativeButtonText
            btnCommonDialogPositive.text = positiveButtonText
        }
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
}
