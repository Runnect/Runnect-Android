package com.runnect.runnect.presentation.login

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityLoginBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_VISITOR
import com.runnect.runnect.util.analytics.EventName.EVENT_VIEW_SOCIAL_LOGIN
import com.runnect.runnect.util.extension.showToast
import com.runnect.runnect.util.preference.AuthUtil.getAccessToken
import com.runnect.runnect.util.preference.AuthUtil.saveToken
import com.runnect.runnect.util.preference.StatusType.LoginStatus
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity :
    BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private lateinit var socialLogin: SocialLogin
    private lateinit var googleLogin: GoogleLogin
    private lateinit var kakaoLogin: KakaoLogin
    private val viewModel: LoginViewModel by viewModels()

    //자동 로그인
    override fun onStart() {
        super.onStart()
        val accessToken = this.getAccessToken()

        when (LoginStatus.getLoginStatus(accessToken)) {
            LoginStatus.EXPIRED -> {
                showToast(getString(R.string.alert_need_to_re_sign))
            }

            LoginStatus.LOGGED_IN -> {
                Timber.d("자동로그인 완료")
                moveToMain()
                showToast(MESSAGE_LOGIN_SUCCESS)
            }

            else -> {}
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics.logClickedItemEvent(EVENT_VIEW_SOCIAL_LOGIN)
        googleLogin = GoogleLogin(
            activity = this@LoginActivity,
            viewModel = viewModel
        )
        kakaoLogin = KakaoLogin(
            context = this@LoginActivity,
            viewModel = viewModel
        )
        addObserver()
        addListener()
    }

    private fun addListener() {
        val ctx: Context = this
        with(binding) {
            cvGoogleLogin.setOnClickListener {
                socialLogin = googleLogin
                socialLogin.signIn()
            }
            cvKakaoLogin.setOnClickListener {
                socialLogin = kakaoLogin
                socialLogin.signIn()
            }
            btnVisitorMode.setOnClickListener {
                Analytics.logClickedItemEvent(EVENT_CLICK_VISITOR)
                ctx.saveToken(
                    accessToken = LoginStatus.VISITOR.value,
                    refreshToken = LoginStatus.VISITOR.value
                )
                moveToMain()
            }
        }
    }


    private fun addObserver() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    when (viewModel.loginResult.value?.type) {
                        "Login" -> handleSuccessfulLogin()
                        "Signup" -> handleSuccessfulSignup()
                    }
                }

                else -> binding.indeterminateBar.isVisible = false
            }
        }
        viewModel.errorMessage.observe(this) {
            Timber.tag(ContentValues.TAG).d("로그인 통신 실패: $it")
        }
    }

    private fun handleSuccessfulLogin() {
        saveSignTokenInfo()
        moveToMain()
        Toast.makeText(this@LoginActivity, MESSAGE_LOGIN_SUCCESS, Toast.LENGTH_SHORT).show()
        binding.indeterminateBar.isVisible = false
    }

    private fun handleSuccessfulSignup() {
        saveSignTokenInfo()
        moveToGiveNickName()
    }

    private fun moveToGiveNickName() {
        val intent = Intent(this, GiveNicknameActivity::class.java)
        intent.putExtra(EXTRA_ACCESS_TOKEN, viewModel.loginResult.value?.accessToken)
        intent.putExtra(EXTRA_REFRESH_TOKEN, viewModel.loginResult.value?.refreshToken)
        startActivity(intent)
        finish()
    }


    private fun saveSignTokenInfo() {
        viewModel.loginResult.value?.let { loginResult ->
            this.saveToken(
                accessToken = loginResult.accessToken,
                refreshToken = loginResult.refreshToken
            )
        }
        Timber.d("ACCESS TOKEN: ${viewModel.loginResult.value?.accessToken}")
        Timber.d("REFRESH TOKEN: ${viewModel.loginResult.value?.refreshToken}")
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::socialLogin.isInitialized) {
            socialLogin.clearSocialLogin()
        }
    }

    companion object {
        const val GOOGLE_SIGN = "GOOGLE"
        const val KAKAO_SIGN = "KAKAO"

        const val EXTRA_ACCESS_TOKEN = "access"
        const val EXTRA_REFRESH_TOKEN = "refresh"

        const val MESSAGE_LOGIN_SUCCESS = "로그인 되었습니다"
    }
}
