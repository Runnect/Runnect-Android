package com.runnect.runnect.data.service

import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.data.dto.response.ResponseGetRefreshToken
import com.runnect.runnect.data.dto.response.base.BaseResponse
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
                saveToken(accessToken = "", refreshToken = "")
                response
            }
        } else {
            response
        }
    }

    private fun Request.newAuthTokenBuilder() =
        runBlocking(Dispatchers.IO) {
            val accessToken = getAccessToken()
            val refreshToken = getNewToken()
            newBuilder().apply {
                addHeader(ACCESS_TOKEN, accessToken)
                addHeader(REFRESH_TOKEN, refreshToken)
            }
        }


    private fun getAccessToken(): String {
        return PreferenceManager.getString(
            ApplicationClass.appContext,
            TOKEN_KEY_ACCESS
        ) ?: ""
    }

    private fun getNewToken(): String {
        return PreferenceManager.getString(
            ApplicationClass.appContext,
            TOKEN_KEY_REFRESH
        ) ?: ""
    }

    private fun saveToken(accessToken: String, refreshToken: String) {
        PreferenceManager.setString(ApplicationClass.appContext, TOKEN_KEY_ACCESS, accessToken)
        PreferenceManager.setString(ApplicationClass.appContext, TOKEN_KEY_REFRESH, refreshToken)
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
            handleGetRefreshTokenFailure(refreshTokenResponse, headerRequest, chain)
        }
    }

    private fun getNewToken(originalRequest: Request, chain: Interceptor.Chain): Response {
        val baseUrl = ApplicationClass.getBaseUrl()
        val refreshToken = getNewToken()
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
                saveToken(it.accessToken, it.refreshToken)
            }
        }

        val newRequest = originalRequest.newAuthTokenBuilder().build()
        return chain.proceed(newRequest)
    }

    private fun handleGetRefreshTokenFailure(
        refreshTokenResponse: Response,
        headerRequest: Request,
        chain: Interceptor.Chain
    ): Response {
        Timber.e("New Refresh Token Failure: ${refreshTokenResponse.code}")
        saveToken("", "")
        return chain.proceed(headerRequest)
    }


    companion object {
        private const val ACCESS_TOKEN = "accessToken"
        private const val CODE_TOKEN_EXPIRED = 401
        private const val REFRESH_TOKEN = "refreshToken"

        const val TOKEN_KEY_ACCESS = "access"
        const val TOKEN_KEY_REFRESH = "refresh"
    }

}
