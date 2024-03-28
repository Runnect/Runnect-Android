package com.runnect.runnect.data.network

import com.runnect.runnect.domain.common.RunnectException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <R, D> Result<R>.mapToFlowResult(
    mapper: (R) -> D
): Flow<Result<D>> = flow {
    val result = when {
        this@mapToFlowResult.isSuccess -> Result.success(
            // CallAdapter에서 body가 null인 경우도 걸러주고 있으므로
            // Result.success의 데이터가 null인 경우는 없을듯함
            mapper(this@mapToFlowResult.getOrNull()!!)
        )

        else -> Result.failure(
            this@mapToFlowResult.exceptionOrNull() ?: RunnectException()
        )
    }

    emit(result)
}