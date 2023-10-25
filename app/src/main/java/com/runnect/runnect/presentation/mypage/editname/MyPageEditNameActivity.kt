package com.runnect.runnect.presentation.mypage.editname

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityMyPageEditNameBinding
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.hideKeyboard
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageEditNameActivity :
    BindingActivity<ActivityMyPageEditNameBinding>(R.layout.activity_my_page_edit_name) {
    private val viewModel: MyPageEditNameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        initLayout()
        addListener()
        addObserver()
    }

    private fun initLayout() {
        val nickName = intent.getStringExtra(EXTRA_NICK_NAME)
        val profileImgResId = intent.getIntExtra(EXTRA_PROFILE, R.drawable.user_profile_basic)
        viewModel.setNickName(nickName = nickName!!)
        viewModel.setProfileImg(profileImgResId = profileImgResId)
    }

    private fun addListener() {
        binding.ivMyPageEditNameBack.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.tvMyPageEditNameFinish.setOnClickListener {
            viewModel.updateNickName()
        }

        binding.etMyPageEditName.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(binding.etMyPageEditName)
                    return true
                }
                return false
            }
        })
    }

    private fun addObserver() {
        viewModel.uiState.observe(this) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    setResult(
                        RESULT_OK,
                        Intent().putExtra(EXTRA_NICK_NAME, viewModel.nickName.value)
                    )
                    finish()
                }

                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    if (viewModel.statusCode.value == 400) {
                        showToast(getString(R.string.my_page_edit_name_redundant_warning))
                    }
                }
            }
        }
        viewModel.nickName.observe(this) {
            with(binding.tvMyPageEditNameFinish) {
                if (it.isNullOrEmpty()) {
                    isActivated = false
                    isClickable = false
                } else {
                    isActivated = true
                    isClickable = true
                }
            }
        }
    }

    //키보드 밖 터치 시, 키보드 내림
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev!!.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                hideKeyboard(focusView)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    companion object {
        const val EXTRA_NICK_NAME = "nickname"
        const val EXTRA_PROFILE = "profile_img"
    }
}