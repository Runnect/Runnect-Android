package com.runnect.runnect.data.network.calladapter

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ResultCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Call<Result<T>>> {

    override fun responseType() = responseType

    // Retrofit의 Call을 Flow<>로 변환
    override fun adapt(call: Call<T>): Call<Result<T>> {
        return ResultCall(call)
    }
}