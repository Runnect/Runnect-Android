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
        val apiResult = suspendCancellableCoroutine { continuation ->
            runCatching {
                call.enqueue(object : Callback<T> {
                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        val result = parseResponse(response)
                        continuation.resume(result)
                    }

                    override fun onFailure(call: Call<T>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
            }.onFailure {
                continuation.resumeWithException(it)
            }

            // Coroutine이 취소 되면 네트워크 요청도 취소
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }

        emit(apiResult)
    }

    private fun parseResponse(response: Response<T>): Result<T> {
        if (response.isSuccessful) {
            return response.body()?.let {
                Result.success(it)
            } ?: Result.failure(
                RunnectException(
                    code = response.code(),
                    message = ERROR_MSG_RESPONSE_IS_NULL
                )
            )
        }

        return Result.failure(parseErrorResponse(response))
    }

    // Response에서 오류를 파싱하여 RunnectException 객체를 생성
    private fun parseErrorResponse(response: Response<*>): RunnectException {
        val code = response.code()
        val errorBodyString = response.errorBody()?.string()

        return errorBodyString?.let { errorBody ->
            parseBaseErrorResponse(code, errorBody)
        } ?: RunnectException(code, ERROR_MSG_COMMON)
    }

    // 일반적인 에러 응답을 파싱하여 RunnectException 생성
    private fun parseBaseErrorResponse(code: Int, errorBody: String): RunnectException? {
        return runCatching {
            gson.fromJson(errorBody, ErrorResponse::class.java)
        }.getOrNull()?.let { errorResponse ->
            RunnectException(
                code = code,
                message = errorResponse.message ?: errorResponse.error ?: ERROR_MSG_COMMON
            )
        }
    }

    companion object {
        private const val ERROR_MSG_COMMON = "알 수 없는 에러가 발생하였습니다."
        private const val ERROR_MSG_RESPONSE_IS_NULL = "데이터를 불러올 수 없습니다."
    }
}