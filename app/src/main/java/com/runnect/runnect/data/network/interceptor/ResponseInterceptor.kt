package com.runnect.runnect.data.network.interceptor

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber

/**
 * BaseResponse에서 data만 추출 (불필요한 래핑 제거)
 * - 서버에서 내려준 형식이 아니라면 응답 그대로 반환
 */
class ResponseInterceptor : Interceptor {

    private val json = Json { ignoreUnknownKeys = true }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (!originalResponse.isSuccessful) return originalResponse

        val bodyString = originalResponse.peekBody(Long.MAX_VALUE).string()
        val newResponseBodyString = extractData(bodyString)?.let {
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

    private fun extractData(body: String): String? {
        return try {
            val jsonObject = json.parseToJsonElement(body).jsonObject
            if (!isBaseResponse(jsonObject)) return null
            jsonObject["data"].toString()
        } catch (e: Exception) {
            null
        }
    }

    private fun isBaseResponse(jsonObject: JsonObject): Boolean {
        val requiredFields = listOf("status", "success", "message", "data")
        return requiredFields.all { it in jsonObject }
    }
}