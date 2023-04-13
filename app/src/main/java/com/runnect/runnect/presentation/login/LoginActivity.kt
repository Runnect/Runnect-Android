package com.runnect.runnect.presentation.login


import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
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
        val GOOGLE_SIGN = "GOOGLE"
        val KAKAO_SIGN = "KAKAO"
    }

    lateinit var socialLogin: SocialLogin
    lateinit var googleLogin: GoogleLogin
    private val viewModel: LoginViewModel by viewModels()

    //자동 로그인
    override fun onStart() {
        super.onStart()
        val accessToken = PreferenceManager.getString(applicationContext, "access")
        if (accessToken != "none") {
            Timber.d("자동로그인 완료")
            moveToMain()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleLogin = GoogleLogin(this@LoginActivity, viewModel)
        addObserver()
        addListener()
    }

    private fun addListener() {
        with(binding) {
            btnSignInGoogle.setOnClickListener {
                socialLogin = googleLogin
                socialLogin.signIn()
            }
        }
    }

    private fun addObserver() {
        viewModel.loginState.observe(this) {
            if (it == UiState.Success) {
                if (viewModel.loginResult.value?.status == 200) {
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
                }
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

    private fun getCurrentUserProfile() {
        val curUser = GoogleSignIn.getLastSignedInAccount(this)
        curUser?.let {
            val email = curUser.email.toString()
            val familyName = curUser.familyName.toString()
            val givenName = curUser.givenName.toString()
            val displayName = curUser.displayName.toString()
            val photoUrl = curUser.photoUrl.toString()

            Timber.tag("현재 로그인 돼있는 유저의 이메일").d(email)
            Timber.tag("현재 로그인 돼있는 유저의 성").d(familyName)
            Timber.tag("현재 로그인 돼있는 유저의 이름").d(givenName)
            Timber.tag("현재 로그인 돼있는 유저의 전체이름").d(displayName)
            Timber.tag("현재 로그인 돼있는 유저의 프로필 사진의 주소").d(photoUrl)
        }
    }


}
