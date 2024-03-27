package com.runnect.runnect.data.network.calladapter

import com.runnect.runnect.data.network.ApiResult
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResultCallAdapterFactory private constructor() : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // suspend로 선언시 Call로 감싸짐
        // 최상위 타입이 Call인지 체크
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        check(returnType is ParameterizedType) { "Call return type must be parameterized as Call<Foo> or Call<out Foo>" }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != ApiResult::class.java) {
            return null
        }

        check(responseType is ParameterizedType) { "ApiResult return type must be parameterized as ApiResult<Foo> or ApiResult<out Foo>" }

        return ApiResultCallAdapter<Any>(
            getParameterUpperBound(
                0,
                responseType
            )
        )
    }

    companion object {
        @JvmStatic
        fun create() = ApiResultCallAdapterFactory()
    }
}