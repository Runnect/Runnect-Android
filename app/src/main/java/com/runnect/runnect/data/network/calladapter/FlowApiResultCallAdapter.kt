package com.runnect.runnect.data.network.calladapter

import com.google.gson.Gson
import com.runnect.runnect.data.network.ApiResult
import com.runnect.runnect.data.dto.response.base.ErrorResponse
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

class FlowApiResultCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Flow<ApiResult<T>>> {

    private val gson = Gson()

    override fun responseType() = responseType

    // Retrofit의 Call을 Flow<>로 변환
    override fun adapt(call: Call<T>): Flow<ApiResult<T>> = flow {
        val apiResult = suspendCancellableCoroutine { continuation ->
            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    // 예외 발생시키고 Coroutine 종료
                    continuation.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val result = if (response.isSuccessful) {
                        response.body()?.let {
                            ApiResult.Success(it)
                        } ?: ApiResult.Failure(code = response.code(), message = "Response body is null")
                    } else {
                        parseErrorResponse(response)
                    }

                    continuation.resume(result)
                }
            })

            // Coroutine이 취소 되면 네트워크 요청도 취소
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }

        emit(apiResult)
    }

    private fun parseErrorResponse(response: Response<*>): ApiResult.Failure {
        val errorJson = response.errorBody()?.string()

        return runCatching {
            val errorBody = gson.fromJson(errorJson, ErrorResponse::class.java)
            val message = errorBody?.run {
                message ?: error ?: "알 수 없는 에러가 발생하였습니다."
            }

            ApiResult.Failure(
                code = errorBody.status,
                message = message
            )
        }.getOrElse {
            ApiResult.Failure(
                code = response.code(),
                message = "알 수 없는 에러가 발생하였습니다."
            )
        }
    }
}