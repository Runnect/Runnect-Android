package com.runnect.runnect.developer.data.service

import com.runnect.runnect.developer.data.dto.ResponseServerStatus
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Url

interface ServerStatusService {

    @GET
    fun checkServerStatus(
        @Url url: String
    ): Flow<Result<ResponseServerStatus>>
}