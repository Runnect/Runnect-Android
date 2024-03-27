package com.runnect.runnect.data.network.calladapter

import com.runnect.runnect.data.network.ApiResult
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ApiResultCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Call<ApiResult<T>>> {

    override fun responseType() = responseType

    // Retrofit의 Call을 Flow<>로 변환
    override fun adapt(call: Call<T>): Call<ApiResult<T>> {
        return ApiResultCall(call)
    }
}