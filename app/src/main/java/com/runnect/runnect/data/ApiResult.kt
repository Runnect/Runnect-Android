package com.runnect.runnect.data

import com.runnect.runnect.domain.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

sealed class ApiResult<out R> {
    data class Success<R>(val body: R) : ApiResult<R>()
    data class Failure(
        val code: Int,
        val message: String?,
    ) : ApiResult<Nothing>()

    // ApiResult<BaseResponse> 형태를 DomainResult<BaseModel> 형태로 매핑
    fun <D> mapToResult(mapper: (R) -> D): Result<D> {
        return when (this) {
            is Success -> {
                Result.Success(mapper(this.body))
            }

            is Failure -> {
                Result.Failure(code, message)
            }
        }
    }

    fun <D> mapToFlowResult(mapper: (R) -> D): Flow<Result<D>> {
        return flow {
            when (this@ApiResult) {
                is Success -> {
                    emit(
                        Result.Success(mapper(body))
                    )
                }

                is Failure -> {
                    emit(
                        Result.Failure(code, message)
                    )
                }
            }
        }
    }
}

fun <R, D> Flow<ApiResult<R>>.mapToResult(mapper: (R) -> D): Flow<Result<D>> {
    return this.map {
        it.mapToResult(mapper)
    }
}