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
import com.runnect.runnect.data.model.RequestPostLoginDto
import com.runnect.runnect.databinding.ActivityLoginBinding
import com.runnect.runnect.presentation.MainActivity
import timber.log.Timber


class LoginActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityLoginBinding>(com.runnect.runnect.R.layout.activity_login) {

    companion object {
        lateinit var accessToken: String
        lateinit var refreshToken: String
    }

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private val viewModel: LoginViewModel by viewModels()

    //기존 로그인 여부 체크 후 바로 MainActivity로 넘어가게 해놨는데, 추후에 카카오 로그인 추가 시 logic에 관련 코드 추가해줘야함.
//    override fun onStart() {
//        super.onStart()
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        account?.let {
//            if (!it.isExpired) {
//                //보니까 자동로그인 때만 죽는 거 같은데?
//                Timber.tag(ContentValues.TAG).d("it.isExpired 값 : ${it.isExpired}")
//
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                Toast.makeText(this, "로그인 되었습니다", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActivityResultLauncher
        setResultSignUp()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        with(binding) {
            btnSignInGoogle.setOnClickListener {
                signIn()
            }
        }
        addObserver()
    }

    private fun setResultSignUp() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                Timber.tag(ContentValues.TAG).d("resultCode 값 : ${it.resultCode}")
                Timber.tag(ContentValues.TAG).d("Activity.RESULT_OK 값 : ${Activity.RESULT_OK}")

                if (it.resultCode == Activity.RESULT_OK) {
                    Timber.tag(ContentValues.TAG).d("정상적으로 돌고있습니다용")
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    handleSignInResult(task)
                } else {
                    Timber.tag(ContentValues.TAG).d("정상적으로 안 돌고있습니다용")
                }
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken
            Timber.tag(ContentValues.TAG).d("유저 idToken 값: $idToken")
            viewModel.postLogin(RequestPostLoginDto(token = idToken, "GOOGLE"
            ))
            // TODO(developer): send ID Token to server and validate
        } catch (e: ApiException) {
            Timber.tag("failed").w("signInResult:failed code=%s", e.statusCode)
        }
    }

    private fun addObserver() {
//        viewModel.loginState.observe(this) {
//            when (it) {
//                UiState.Empty -> hideLoadingBar()
//                UiState.Loading -> showLoadingBar()
//                UiState.Success -> {
//                    hideLoadingBar()
//                    notifyUploadFinish()
//                }
//                UiState.Failure -> {
//                    hideLoadingBar()
//                    showErrorMessage()
//                }
//            }
//        }

        viewModel.loginResult.observe(this) {
            Timber.tag(ContentValues.TAG).d("로그인 통신 결과: ${it.message}")
            if (it.message == "로그인 성공") {
                accessToken = it.data.accessToken
                refreshToken = it.data.refreshToken

                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
                }
                startActivity(intent)
                Toast.makeText(this, "로그인 되었습니다", Toast.LENGTH_SHORT).show()
            }

        }

        viewModel.errorMessage.observe(this) {
            Timber.tag(ContentValues.TAG).d("로그인 통신 실패: $it")
        }

    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            //...
        }
    }

    private fun revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this) {
            //...
        }
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
