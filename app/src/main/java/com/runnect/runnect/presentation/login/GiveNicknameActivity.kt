package com.runnect.runnect.presentation.login

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.runnect.runnect.R
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityGiveNicknameBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.hideKeyboard
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GiveNicknameActivity :
    BindingActivity<ActivityGiveNicknameBinding>(R.layout.activity_give_nickname) {
    private val viewModel: GiveNickNameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        addListener()
        addObserver()
    }

    private fun addListener() {
        binding.tvGiveNicknameFinish.setOnClickListener {
            viewModel.updateNickName()
        }
    }

    private fun saveSignTokenInfo() {
        PreferenceManager.setString(
            applicationContext,
            TOKEN_KEY_ACCESS,
            intent.getStringExtra("access")
        )
        PreferenceManager.setString(
            applicationContext,
            TOKEN_KEY_REFRESH,
            intent.getStringExtra("refresh")
        )
    }

    private fun addObserver() {
        viewModel.nickName.observe(this) {
            with(binding.tvGiveNicknameFinish) {
                if (it.isNullOrEmpty()) {
                    isActivated = false
                    isClickable = false
                } else {
                    isActivated = true
                    isClickable = true
                }
            }
        }
        viewModel.uiState.observe(this) { state ->
            when (state) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false
                UiState.Loading -> {
                    with(binding) {
                        indeterminateBar.isVisible = true
                        tvGiveNicknameFinish.isClickable = false
                    }
                }

                UiState.Success -> handleSuccessfulSignup()
                UiState.Failure -> handleUnSuccessfulSignup()
            }
        }
    }

    private fun handleSuccessfulSignup() {
        saveSignTokenInfo()
        showToast("회원가입 되었습니다")
        binding.indeterminateBar.isVisible = false
        moveToMain()
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
        }
        startActivity(intent)
        finish()
    }

    private fun handleUnSuccessfulSignup() {
        binding.indeterminateBar.isVisible = false
        if (viewModel.statusCode.value == 400) {
            showToast(getString(R.string.my_page_edit_name_redundant_warning))
        }
        binding.tvGiveNicknameFinish.isClickable = true
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

    companion object {
        const val TOKEN_KEY_ACCESS = "access"
        const val TOKEN_KEY_REFRESH = "refresh"
    }
}

