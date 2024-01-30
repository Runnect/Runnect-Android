package com.runnect.runnect.presentation.login

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityLoginBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_GOOGLE_LOGIN
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_KAKAO_LOGIN
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_VISITOR
import com.runnect.runnect.util.analytics.EventName.EVENT_VIEW_SOCIAL_LOGIN
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : BindingActivity<ActivityLoginBinding>(com.runnect.runnect.R.layout.activity_login) {
    lateinit var socialLogin: SocialLogin
    lateinit var googleLogin: GoogleLogin
    lateinit var kakaoLogin: KakaoLogin
    private val viewModel: LoginViewModel by viewModels()

    //자동 로그인
    override fun onStart() {
        super.onStart()
        val accessToken = PreferenceManager.getString(applicationContext, TOKEN_KEY_ACCESS)
        if (accessToken != "none" && accessToken != "visitor") {
            Timber.d("자동로그인 완료")
            moveToMain()
            Toast.makeText(this@LoginActivity, MESSAGE_LOGIN_SUCCESS, Toast.LENGTH_SHORT).show()
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
                PreferenceManager.setString(
                    context = applicationContext,
                    key = TOKEN_KEY_ACCESS,
                    value = "visitor"
                )
                PreferenceManager.setString(
                    context = applicationContext,
                    key = TOKEN_KEY_REFRESH,
                    value = "visitor"
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
        PreferenceManager.setString(
            context = applicationContext,
            key = TOKEN_KEY_ACCESS,
            value = viewModel.loginResult.value?.accessToken
        )
        PreferenceManager.setString(
            context = applicationContext,
            key = TOKEN_KEY_REFRESH,
            value = viewModel.loginResult.value?.refreshToken
        )
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

        const val TOKEN_KEY_ACCESS = "access"
        const val TOKEN_KEY_REFRESH = "refresh"

        const val EXTRA_ACCESS_TOKEN = "access"
        const val EXTRA_REFRESH_TOKEN = "refresh"

        const val MESSAGE_LOGIN_SUCCESS = "로그인 되었습니다"
    }
}
