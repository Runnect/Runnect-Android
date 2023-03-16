package com.runnect.runnect.presentation.login


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.databinding.ActivityLoginBinding
import timber.log.Timber


class LoginActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityLoginBinding>(com.runnect.runnect.R.layout.activity_login) {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        account?.let {
            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "Not Yet", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActivityResultLauncher
        setResultSignUp()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
                .requestEmail()
                .requestProfile()
                .build()

            mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        with(binding) {
            btnLogin.setOnClickListener {
                signIn()
            }
        }
        }

    private fun setResultSignUp(){
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Timber.tag(ContentValues.TAG).d("resultCode 값 : ${it.resultCode}")
            Timber.tag(ContentValues.TAG).d("Activity.RESULT_OK 값 : ${Activity.RESULT_OK}")

            //정상적으로 결과가 받아와진다면 조건문 실행
            if(it.resultCode == Activity.RESULT_OK) {
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
            val email = account?.email
            val idToken = account.idToken
            Timber.tag(ContentValues.TAG).d("이메일입니다용 : $email")
            Timber.tag(ContentValues.TAG).d("토근입니다용 : $idToken")
            // TODO(developer): send ID Token to server and validate
        } catch (e: ApiException) {
            Timber.tag("failed").w("signInResult:failed code=%s", e.statusCode)
        }
    }
//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>){
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//            val email = account?.email.toString()
//            val familyName = account?.familyName.toString()
//            val givenName = account?.givenName.toString()
//            val displayName = account?.displayName.toString()
//            val photoUrl = account?.photoUrl.toString()
//
//            Timber.tag("로그인한 유저의 이메일").d(email)
//            Timber.tag("로그인한 유저의 성").d(familyName)
//            Timber.tag("로그인한 유저의 이름").d(givenName)
//            Timber.tag("로그인한 유저의 전체이름").d(displayName)
//            Timber.tag("로그인한 유저의 프로필 사진의 주소").d(photoUrl)
//        } catch (e: ApiException) {
//            Timber.tag("failed").w("signInResult:failed code=%s", e.statusCode)
//        }
//    }

    private fun signIn(){
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun signOut(){
        mGoogleSignInClient.signOut().
                addOnCompleteListener(this){
                    //...
                }
    }

    private fun revokeAccess(){
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this){
            //...
        }
    }

    private fun getCurrentUserProfile(){
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
