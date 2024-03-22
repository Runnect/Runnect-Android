package com.runnect.runnect.data.interceptor

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.runnect.runnect.data.dto.response.base.BaseResponse
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber

/**
 * BaseResponse에서 data만 추출 (불필요한 래핑 제거)
 * 서버에서 내려준 형식이 아니라면 응답 그대로 반환
 */
class ResponseInterceptor : Interceptor {

    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (!originalResponse.isSuccessful) return originalResponse

        val bodyString = originalResponse.peekBody(Long.MAX_VALUE).string()
        val newResponseBodyString = jsonToBaseResponse(bodyString)?.let {
            it.toResponseBody("application/json".toMediaTypeOrNull())
        } ?: return originalResponse

        return originalResponse.newBuilder()
            .code(originalResponse.code)
            .body(newResponseBodyString)
            .build()
            .apply {
                Timber.v("""\n
                    origin = ${originalResponse.peekBody(Long.MAX_VALUE).string()}
                    new = ${this.peekBody(Long.MAX_VALUE).string()}
                    """.trimIndent()
                )
            }
    }

    private fun jsonToBaseResponse(body: String): String? {
        return try {
            val baseResponse = gson.fromJson(body, BaseResponse::class.java)
            gson.toJson(baseResponse.data)
        } catch (e: JsonSyntaxException) {
            null // JSON 구문 분석 오류 발생 시 원래 형식을 반환
        } catch (e: JsonParseException) {
            null // JSON 파싱 오류 발생 시 원래 형식을 반환
        } catch (e: Exception) {
            null // 기타 예외 발생 시 원래 형식을 반환
        }
    }
}