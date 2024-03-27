package com.runnect.runnect.data.network

import com.runnect.runnect.domain.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class ApiResult<out R> {
    data class Success<R>(val body: R) : ApiResult<R>()
    data class Failure(
        val code: Int,
        val message: String?,
    ) : ApiResult<Nothing>()

    // ApiResult<BaseResponse> 형태를 Flow<DomainResult<BaseModel>> 형태로 매핑
    fun <D> mapToFlowResult(
        mapper: (R) -> D
    ): Flow<Result<D>> = flow {
        emit(
            when (this@ApiResult) {
                is Success -> Result.Success(mapper(body))
                is Failure -> Result.Failure(code, message)
            }
        )
    }
}