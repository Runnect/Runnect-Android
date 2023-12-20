package com.runnect.runnect.presentation.login

import android.content.ContentValues
import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient.Companion.instance
import com.runnect.runnect.data.dto.request.RequestPostLogin
import timber.log.Timber
import java.lang.ref.WeakReference

class KakaoLogin(context: Context, viewModel: LoginViewModel) : SocialLogin {
    private val contextRef = WeakReference(context)
    private val viewModelRef = WeakReference(viewModel)

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Timber.e("카카오 로그인 실패", error)
        } else if (token != null) {
            Timber.d("카카오 로그인 성공")
            viewModelRef.get()?.postLogin(
                RequestPostLogin(
                    token.accessToken, LoginActivity.KAKAO_SIGN
                )
            )
        }
    }

    override fun signIn() {
        /**
         * 카카오톡 설치 여부에 따라서 설치 되어있으면 카카오톡 로그인을 시도한다.
         * 미설치 시 카카오 계정 로그인을 시도한다.
         * loginWithKakao: 카카오톡이 설치되어 있다면 카카오톡 로그인을 시도하고, 그렇지 않다면 카카오 계정 로그인을 시도한다.
         * 카카오톡 로그인에 실패하면 사용자가 의도적으로 로그인 취소한 경우를 제외하고는 카카오 계정 로그인을 서브로 실행한다.
         */
        if (contextRef.get()?.let { instance.isKakaoTalkLoginAvailable(it) } == true) {
            instance.loginWithKakaoTalk(contextRef.get()!!) { token, error ->
                if (error != null) {
                    Timber.e(ContentValues.TAG, "카카오톡 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    instance.loginWithKakaoAccount(contextRef.get()!!, callback = callback)
                } else if (token != null) {
                    Timber.d("카카오톡 로그인")
                    instance.loginWithKakaoTalk(contextRef.get()!!, callback = callback)
                }
            }
        } else {
            Timber.d("카카오 계정 로그인")
            contextRef.get()?.let { instance.loginWithKakaoAccount(it, callback = callback) }
        }
    }

    override fun clearSocialLogin() {
        contextRef.clear()
        viewModelRef.clear()
    }
}