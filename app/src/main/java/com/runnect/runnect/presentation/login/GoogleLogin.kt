package com.runnect.runnect.presentation.login

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.data.dto.request.RequestLogin
import timber.log.Timber
import java.lang.ref.WeakReference

class GoogleLogin(activity: LoginActivity, viewModel: LoginViewModel) : SocialLogin {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val activityRef = WeakReference(activity)
    private val viewModelRef = WeakReference(viewModel)

    init {
        activityRef.get()?.let {
            setClient(it)
            setLauncher(it)
        }
    }

    //구글 클라이언트 옵션 세팅
    private fun setClient(activity: LoginActivity) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    //구글 런쳐 세팅
    private fun setLauncher(activity: LoginActivity) {
        resultLauncher =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    handleSignInResult(task)
                }
            }
    }

    //구글 런쳐에서 받은 token으로 Runnect post호출
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val socialToken = account.idToken
            viewModelRef.get()?.postLogin(
                RequestLogin(
                    token = socialToken,
                    provider = LoginActivity.GOOGLE_SIGN
                )
            )
        } catch (e: ApiException) {
            Timber.tag("failed").w("signInResult:failed code=%s", e.statusCode)
        }
    }

    //구글 로그인 런쳐 실행
    override fun signIn() {
        val intent: Intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(intent)
    }

    override fun clearSocialLogin() {
        mGoogleSignInClient.revokeAccess()
        resultLauncher.unregister()
        activityRef.clear()
        viewModelRef.clear()
    }
}