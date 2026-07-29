package com.runnect.runnect.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.runnect.runnect.R
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName
import com.runnect.runnect.util.analytics.EventName.Param
import com.runnect.runnect.util.extension.showToast
import com.runnect.runnect.util.preference.AuthUtil.saveToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GiveNicknameActivity : AppCompatActivity() {
    private val viewModel: GiveNickNameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Analytics.logEvent(EventName.VIEW_GIVE_NICKNAME)
        addObserver()

        setContent {
            val nickName by viewModel.nickName.observeAsState("")
            val uiState by viewModel.uiState.observeAsState(UiState.Empty)

            RunnectTheme {
                GiveNicknameScreen(
                    state = GiveNicknameUiState.from(
                        nickName = nickName,
                        uiState = uiState
                    ),
                    onNickNameChange = viewModel::updateNickNameInput,
                    onStartClick = viewModel::updateNickName
                )
            }
        }
    }

    private fun saveSignTokenInfo() {
        this.saveToken(
            accessToken = intent.getStringExtra("access") ?: "",
            refreshToken = intent.getStringExtra("refresh") ?: ""
        )
    }

    private fun addObserver() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                UiState.Empty,
                UiState.Loading -> Unit
                UiState.Success -> handleSuccessfulSignup()
                UiState.Failure -> handleUnSuccessfulSignup()
            }
        }
    }

    private fun handleSuccessfulSignup() {
        Analytics.logEvent(
            EventName.ACTION_NICKNAME_COMPLETE,
            Param.NICKNAME_LENGTH to (viewModel.nickName.value?.length ?: 0)
        )
        saveSignTokenInfo()
        showToast("회원가입 되었습니다")
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
        if (viewModel.statusCode.value == 400) {
            showToast(getString(R.string.my_page_edit_name_redundant_warning))
        }
    }

}
