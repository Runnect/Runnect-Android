package com.runnect.runnect.presentation.mypage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingDialog
import com.runnect.runnect.databinding.DialogMyPageEditNameBinding
import com.runnect.runnect.util.extension.clearFocus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageEditNameDialog :
    BindingDialog<DialogMyPageEditNameBinding>(R.layout.dialog_my_page_edit_name) {
    private val viewModel: MyPageViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initDialog()
        addListener()
    }

    private fun initDialog() {
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.etMyPageDialogEditName.setText(viewModel.nickName.value.toString())
    }

    private fun addListener() {
        //키보드 완료 버튼 클릭 시 이벤트 실행 후 키보드 내리기
        //추후 showToast -> 뷰모델의 닉네임 변경 API 호출 대체 예정
        binding.etMyPageDialogEditName.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //입력된 수정 닉네임의 길이가 최소 한 글자 이상이어야 뷰모델의 nickname에 반영되도록 함
                    if (!binding.etMyPageDialogEditName.text.isNullOrEmpty()) {
                        viewModel.nickName.value = binding.etMyPageDialogEditName.text.toString()
                        viewModel.updateNickName()
                    }
                    requireContext().clearFocus(binding.etMyPageDialogEditName)
                    dialog!!.dismiss()
                    return true
                }
                return false
            }
        })
    }
}