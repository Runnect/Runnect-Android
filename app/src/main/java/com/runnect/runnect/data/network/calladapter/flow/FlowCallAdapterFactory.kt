package com.runnect.runnect.data.network.calladapter.flow

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
        // 최상위 타입이 Flow인지 체크
        if (getRawType(returnType) != Flow::class.java) {
            return null
        }

        check(returnType is ParameterizedType) {
            "Flow return type must be parameterized as Flow<Foo> or Flow<out Foo>"
        }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != Result::class.java) {
            return null
        }

        check(responseType is ParameterizedType) {
            "ApiResult return type must be parameterized as ApiResult<Foo> or ApiResult<out Foo>"
        }

        return FlowCallAdapter<Any>(
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