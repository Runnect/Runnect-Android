package com.runnect.runnect.presentation.login

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task

interface SocialLogin {
    fun setClient(activity: LoginActivity)
    fun setLauncher(activity: LoginActivity)
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>)
    fun signIn()
    fun unregisterActivityResultLauncher()
}