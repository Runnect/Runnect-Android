package com.runnect.runnect.data.network.calladapter

import com.google.gson.Gson
import com.runnect.runnect.data.dto.response.base.ErrorResponse
import com.runnect.runnect.domain.common.RunnectException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.Request
import okio.Timeout

class ResultCall<T>(private val call: Call<T>) : Call<Result<T>> {

    private val gson = Gson()

    override fun execute(): Response<Result<T>> {
        throw UnsupportedOperationException("ResultCall doesn't support execute")
    }

    override fun enqueue(callback: Callback<Result<T>>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val apiResult = if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(
                        RunnectException(
                            code = response.code(),
                            message = ERROR_MSG_RESPONSE_IS_NULL
                        )
                    )
                } else {
                    Result.failure(parseErrorResponse(response))
                }

                callback.onResponse(
                    this@ResultCall,
                    Response.success(apiResult)
                )
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onFailure(this@ResultCall, t)
            }
        })
    }

    private fun parseErrorResponse(response: Response<*>): RunnectException {
        val errorJson = response.errorBody()?.string()

        return runCatching {
            val errorBody = gson.fromJson(errorJson, ErrorResponse::class.java)
            val message = errorBody?.run {
                message ?: error ?: ERROR_MSG_COMMON
            }

            RunnectException(
                code = errorBody.status,
                message = message
            )
        }.getOrElse {
            RunnectException(
                code = response.code(),
                message = ERROR_MSG_COMMON
            )
        }
    }

    override fun clone(): Call<Result<T>> = ResultCall(call.clone())
    override fun isExecuted(): Boolean = call.isExecuted
    override fun cancel() = call.cancel()
    override fun isCanceled(): Boolean = call.isCanceled
    override fun request(): Request = call.request()
    override fun timeout(): Timeout = call.timeout()

    companion object {
        private const val ERROR_MSG_COMMON = "알 수 없는 에러가 발생하였습니다."
        private const val ERROR_MSG_RESPONSE_IS_NULL = "데이터를 불러올 수 없습니다."
    }
}