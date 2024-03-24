package com.runnect.runnect.data.service

import android.content.Context
import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.data.dto.response.ResponseGetRefreshToken
import com.runnect.runnect.data.dto.response.base.BaseResponse
import com.runnect.runnect.util.preference.AuthUtil.getAccessToken
import com.runnect.runnect.util.preference.AuthUtil.getNewToken
import com.runnect.runnect.util.preference.AuthUtil.saveToken
import com.runnect.runnect.util.preference.StatusType.LoginStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val context: Context,
    private val json: Json
) : Interceptor {
    // access Header 에 보내고 이때 401(토큰 만료) 뜨면 액세스 재발급 요청
    // 재발급 성공 : 저장
    // 재발급 실패 : 재 로그인 토스트 메시지 띄우고 preference 빈 값 넣고 로그인 화면 이동
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val headerRequest = originalRequest.newAuthTokenBuilder()
            .build()

        val response = headerRequest.let { chain.proceed(it) }

        return if (response.code == CODE_TOKEN_EXPIRED) {
            try {
                Timber.e("Access Token Expired: getNewAccessToken")
                response.close()
                handleTokenExpired(chain, originalRequest, headerRequest)
            } catch (t: Throwable) {
                Timber.e("Exception: ${t.message}")
                context.saveToken(
                    accessToken = LoginStatus.EXPIRED.value,
                    refreshToken = LoginStatus.EXPIRED.value
                )
                response
            }
        } else {
            response
        }
    }

    private fun Request.newAuthTokenBuilder() =
        runBlocking(Dispatchers.IO) {
            val accessToken = context.getAccessToken()
            val refreshToken = context.getNewToken()
            newBuilder().apply {
                addHeader(ACCESS_TOKEN, accessToken)
                addHeader(REFRESH_TOKEN, refreshToken)
            }
        }


    private fun handleTokenExpired(
        chain: Interceptor.Chain,
        originalRequest: Request,
        headerRequest: Request
    ): Response {
        val refreshTokenResponse = getNewToken(originalRequest, chain)
        return if (refreshTokenResponse.isSuccessful) {
            handleGetRefreshTokenSuccess(refreshTokenResponse, originalRequest, chain)
        } else {
            handleGetNewTokenFailure(refreshTokenResponse, headerRequest, chain)
        }
    }

    private fun getNewToken(originalRequest: Request, chain: Interceptor.Chain): Response {
        val baseUrl = ApplicationClass.getBaseUrl()
        val refreshToken = context.getNewToken()
        val refreshTokenRequest = originalRequest.newBuilder().post("".toRequestBody())
            .url("$baseUrl/api/auth/getNewToken")
            .addHeader(REFRESH_TOKEN, refreshToken)
            .build()

        return chain.proceed(refreshTokenRequest)
    }

    private fun handleGetRefreshTokenSuccess(
        refreshTokenResponse: Response,
        originalRequest: Request,
        chain: Interceptor.Chain
    ): Response {
        refreshTokenResponse.use { response ->
            val responseToken = json.decodeFromString<BaseResponse<ResponseGetRefreshToken>>(
                response.body?.string().orEmpty()
            )
            responseToken.data?.data?.let {
                Timber.e("New Refresh Token Success: ${it.refreshToken}")
                context.saveToken(it.accessToken, it.refreshToken)
            }
        }

        val newRequest = originalRequest.newAuthTokenBuilder().build()
        return chain.proceed(newRequest)
    }

    private fun handleGetNewTokenFailure(
        refreshTokenResponse: Response,
        headerRequest: Request,
        chain: Interceptor.Chain
    ): Response {
        Timber.e("New Refresh Token Failure: ${refreshTokenResponse.code}")
        context.saveToken(
            accessToken = LoginStatus.EXPIRED.value,
            refreshToken = LoginStatus.EXPIRED.value
        )
        return chain.proceed(headerRequest)
    }


    companion object {
        private const val ACCESS_TOKEN = "accessToken"
        private const val CODE_TOKEN_EXPIRED = 401
        private const val REFRESH_TOKEN = "refreshToken"
    }

}
