package com.runnect.runnect.data.network.calladapter

import com.google.gson.Gson
import com.runnect.runnect.data.dto.response.base.ErrorResponse
import com.runnect.runnect.data.network.ApiResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.Request
import okio.Timeout

class ApiResultCall<T>(private val call: Call<T>) : Call<ApiResult<T>> {

    private val gson = Gson()

    override fun execute(): Response<ApiResult<T>> {
        throw UnsupportedOperationException("ResultCall doesn't support execute")
    }

    override fun enqueue(callback: Callback<ApiResult<T>>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val apiResult = if (response.isSuccessful) {
                    response.body()?.let {
                        ApiResult.Success(it)
                    } ?: ApiResult.Failure(code = response.code(), message = "Response body is null")
                } else {
                    parseErrorResponse(response)
                }

                callback.onResponse(
                    this@ApiResultCall,
                    Response.success(apiResult)
                )
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onFailure(this@ApiResultCall, t)
            }
        })
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

    override fun clone(): Call<ApiResult<T>> = ApiResultCall(call.clone())
    override fun isExecuted(): Boolean = call.isExecuted
    override fun cancel() = call.cancel()
    override fun isCanceled(): Boolean = call.isCanceled
    override fun request(): Request = call.request()
    override fun timeout(): Timeout = call.timeout()
}