package com.runnect.runnect.data.network.calladapter.flow

import com.google.gson.Gson
import com.runnect.runnect.data.dto.response.base.ErrorResponse
import com.runnect.runnect.domain.common.RunnectException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
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

    private val gson = Gson()
    override fun responseType() = responseType

    // Retrofit의 Call을 Result<>로 변환
    override fun adapt(call: Call<T>): Flow<Result<T>> = flow {
        emit(flowApiCall(call))
    }

    private suspend fun flowApiCall(call: Call<T>): Result<T> {
        return suspendCancellableCoroutine { continuation ->
            runCatching {
                call.enqueue(object : Callback<T> {
                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        continuation.resume(parseResponse(response))
                    }

                    override fun onFailure(call: Call<T>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
            }.onFailure {
                continuation.resumeWithException(it)
            }

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

    // Response에서 오류를 파싱하여 RunnectException 객체를 생성
    private fun parseErrorResponse(response: Response<*>): RunnectException {
        val errorBodyString = response.errorBody()?.string()
        val errorResponse = errorBodyString?.let {
            gson.fromJson(it, ErrorResponse::class.java)
        }

        val errorMessage = errorResponse?.message ?: errorResponse?.error ?: ERROR_MSG_COMMON
        return RunnectException(response.code(), errorMessage)
    }

    companion object {
        private const val ERROR_MSG_COMMON = "알 수 없는 에러가 발생하였습니다."
        private const val ERROR_MSG_RESPONSE_IS_NULL = "데이터를 불러올 수 없습니다."
    }
}