package com.runnect.runnect.presentation.mypage.editname

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.runnect.runnect.R
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName
import com.runnect.runnect.util.analytics.EventName.Param
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageEditNameActivity : AppCompatActivity() {
    private val viewModel: MyPageEditNameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Analytics.logEvent(EventName.VIEW_EDIT_PROFILE)

        val nickname = intent.getStringExtra(EXTRA_NICK_NAME) ?: ""
        val profileImgResId = intent.getIntExtra(EXTRA_PROFILE, R.drawable.user_profile_basic)
        viewModel.intent(EditNameIntent.Init(nickname, profileImgResId))

        setContent {
            RunnectTheme {
                val state by viewModel.state.collectAsState()
                MyPageEditNameScreen(
                    state = state,
                    onBackClick = {
                        setResult(RESULT_CANCELED)
                        finish()
                    },
                    onNicknameChange = { viewModel.intent(EditNameIntent.UpdateNickname(it)) },
                    onSubmitClick = { viewModel.intent(EditNameIntent.Submit) },
                )
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is EditNameEffect.NavigateSuccess -> {
                            Analytics.logEvent(
                                EventName.ACTION_EDIT_PROFILE_COMPLETE,
                                Param.CHANGED_FIELDS to "nickname"
                            )
                            setResult(
                                RESULT_OK,
                                Intent().putExtra(EXTRA_NICK_NAME, effect.newNickname)
                            )
                            finish()
                        }
                        EditNameEffect.ShowDuplicateError -> {
                            showToast(getString(R.string.my_page_edit_name_redundant_warning))
                        }
                    }
                }
            }
        }
    }

    @Deprecated("Use onBackPressedDispatcher")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    companion object {
        const val EXTRA_NICK_NAME = "nickname"
        const val EXTRA_PROFILE = "profile_img"
    }
}
