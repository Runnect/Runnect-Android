package com.runnect.runnect.data.calladapter.flow

import com.runnect.runnect.data.ApiResult
import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class FlowCallAdapterFactory private constructor() : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // Api Service 메소드의 반환형의 최상위 타입이 Flow 인지 체크
        // suspend가 아니므로 Call 체크 필요 x
        if (getRawType(returnType) != Flow::class.java) {
            return null
        }

        check(returnType is ParameterizedType) { "Flow return type must be parameterized as Flow<Foo> or Flow<out Foo>" }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != ApiResult::class.java) {
            return null
        }

        check(responseType is ParameterizedType) { "ApiResult return type must be parameterized as ApiResult<Foo> or ApiResult<out Foo>" }

        return FlowApiResultCallAdapter<Any>(
            getParameterUpperBound(
                0,
                responseType
            )
        )
    }

    companion object {
        @JvmStatic
        fun create() = FlowCallAdapterFactory()
    }
}