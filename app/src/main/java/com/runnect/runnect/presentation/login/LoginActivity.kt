package com.runnect.runnect.presentation.login


import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.databinding.ActivityLoginBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityLoginBinding>(com.runnect.runnect.R.layout.activity_login) {

    companion object {
        const val GOOGLE_SIGN = "GOOGLE"
        const val KAKAO_SIGN = "KAKAO"
    }

    lateinit var socialLogin: SocialLogin
    lateinit var googleLogin: GoogleLogin
    lateinit var kakaoLogin: KakaoLogin
    private val viewModel: LoginViewModel by viewModels()

    //자동 로그인
    override fun onStart() {
        super.onStart()
        val accessToken = PreferenceManager.getString(applicationContext, "access")
        if (accessToken != "none" && accessToken != "visitor") {
            Timber.d("자동로그인 완료")
            moveToMain()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleLogin = GoogleLogin(this@LoginActivity, viewModel)
        kakaoLogin = KakaoLogin(this@LoginActivity, viewModel)
        addObserver()
        addListener()
    }

    private fun addListener() {
        with(binding) {
            btnSignInGoogle.setOnClickListener {
                socialLogin = googleLogin
                socialLogin.signIn()
            }
            btnSignInKakao.setOnClickListener {
                socialLogin = kakaoLogin
                socialLogin.signIn()
            }
            btnVisitorMode.setOnClickListener {
                PreferenceManager.setString(
                    applicationContext,
                    "access",
                    "visitor"
                )
                PreferenceManager.setString(
                    applicationContext,
                    "refresh",
                    "null"
                )
                moveToMain()
            }
        }
    }

    private fun addObserver() { //방문자 모드는 여기 logic에 해당이 안 돼.
        viewModel.loginState.observe(this) { state ->
            when (state) {
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    PreferenceManager.setString(
                        applicationContext,
                        "access",
                        viewModel.loginResult.value?.accessToken
                    )
                    PreferenceManager.setString(
                        applicationContext,
                        "refresh",
                        viewModel.loginResult.value?.refreshToken
                    )
                    moveToMain()
                    binding.indeterminateBar.isVisible = false
                }
                else -> binding.indeterminateBar.isVisible = false
            }
        }
        viewModel.errorMessage.observe(this) {
            Timber.tag(ContentValues.TAG).d("로그인 통신 실패: $it")
        }
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
        }
        startActivity(intent)
        finish()
        showToast("로그인 되었습니다")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::socialLogin.isInitialized) {
            socialLogin.clearSocialLogin()
        }
    }
}
