package com.runnect.runnect.data.network.calladapter.flow

import com.runnect.runnect.domain.common.RunnectException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FlowCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Flow<Result<T>>> {

    private val json = Json { ignoreUnknownKeys = true }
    override fun responseType() = responseType

    // Retrofit의 Call을 Result<>로 변환
    override fun adapt(call: Call<T>): Flow<Result<T>> = flow {
        emit(flowApiCall(call))
    }

    private suspend fun flowApiCall(call: Call<T>): Result<T> {
        return suspendCancellableCoroutine { continuation ->
            call.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    continuation.resume(parseResponse(response))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })

            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }

    private fun parseResponse(response: Response<T>): Result<T> {
        val nullBodyException by lazy {
            RunnectException(response.code(), ERROR_MSG_RESPONSE_IS_NULL)
        }

        if (!response.isSuccessful) {
            return Result.failure(parseErrorResponse(response))
        }

        return response.body()?.let {
            Result.success(it)
        } ?: Result.failure(nullBodyException)
    }

    private fun parseErrorResponse(response: Response<*>): RunnectException {
        val errorBodyString = response.errorBody()?.string()

        return runCatching {
            val jsonObject = json.parseToJsonElement(errorBodyString.orEmpty()).jsonObject
            val message = jsonObject["message"]?.jsonPrimitive?.content
                ?: jsonObject["error"]?.jsonPrimitive?.content
                ?: ERROR_MSG_COMMON
            RunnectException(response.code(), message)
        }.getOrElse {
            RunnectException(response.code(), ERROR_MSG_COMMON)
        }
    }

    companion object {
        private const val ERROR_MSG_COMMON = "알 수 없는 에러가 발생하였습니다."
        private const val ERROR_MSG_RESPONSE_IS_NULL = "데이터를 불러올 수 없습니다."
    }
}