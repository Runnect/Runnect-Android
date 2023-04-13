package com.runnect.runnect.presentation.login


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.data.dto.request.RequestLogin
import com.runnect.runnect.databinding.ActivityLoginBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityLoginBinding>(com.runnect.runnect.R.layout.activity_login) {

    companion object {
        val GOOGLE_SIGN = "GOOGLE"
        val KAKAO_SIGN = "GOOGLE"
    }

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private val viewModel: LoginViewModel by viewModels()

    //자동 로그인
    override fun onStart() {
        super.onStart()
        val accessToken = PreferenceManager.getString(applicationContext, "access")
        val refreshToken = PreferenceManager.getString(applicationContext, "refresh")
        Timber.d("엑세스 토큰 $accessToken")
        Timber.d("리프레시 토큰 $refreshToken")
        if (accessToken != "none") {
            Timber.d("자동로그인 완료")
            moveToMain()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ActivityResultLauncher
        setResultGoogleSignLauncher()
        setGoogleClient()
        addObserver()
        addListener()
    }

    private fun addListener() {
        with(binding) {
            btnSignInGoogle.setOnClickListener {
                googleSign()
            }
        }
    }

    //구글 클라이언트 옵션 세팅
    private fun setGoogleClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    //구글 런쳐 세팅
    private fun setResultGoogleSignLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    handleGoogleSignInResult(task)
                }
            }
    }

    //구글 런쳐에서 받은 결과로 Runnect post
    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken
            viewModel.postLogin(
                RequestLogin(
                    idToken, GOOGLE_SIGN
                )
            )
        } catch (e: ApiException) {
            Timber.tag("failed").w("signInResult:failed code=%s", e.statusCode)
        }
    }

    //구글 로그인 런쳐 실행
    private fun googleSign() {
        val intent: Intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(intent)
    }

    private fun addObserver() {
        viewModel.loginState.observe(this) {
            if (it == UiState.Success) {
                Timber.d("옵저버 ${viewModel.loginResult.value?.accessToken}")
                Timber.d("옵저버 ${viewModel.loginResult.value?.refreshToken}")
                PreferenceManager.setString(
                    applicationContext, "access",
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
        viewModel.errorMessage.observe(this) {
            Timber.tag(ContentValues.TAG).d("로그인 통신 실패: $it")
        }
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
        }
        startActivity(intent)
        Toast.makeText(this, "로그인 되었습니다", Toast.LENGTH_SHORT).show()
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
