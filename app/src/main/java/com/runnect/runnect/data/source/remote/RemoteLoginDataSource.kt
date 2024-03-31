package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.dto.request.RequestPostLogin
import com.runnect.runnect.data.dto.response.ResponsePostLogin
import com.runnect.runnect.data.service.v2.LoginV2Service
import javax.inject.Inject

class RemoteLoginDataSource @Inject constructor(
    private val loginV2Service: LoginV2Service
) {
    suspend fun postLogin(
        requestPostLogin: RequestPostLogin
    ): Result<ResponsePostLogin> = loginV2Service.postLogin(requestPostLogin)
}