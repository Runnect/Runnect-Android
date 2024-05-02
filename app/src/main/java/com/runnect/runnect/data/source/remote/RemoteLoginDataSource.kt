package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.dto.request.RequestPostLogin
import com.runnect.runnect.data.dto.response.ResponsePostLogin
import com.runnect.runnect.data.service.LoginService
import javax.inject.Inject

class RemoteLoginDataSource @Inject constructor(
    private val loginService: LoginService
) {
    suspend fun postLogin(
        requestPostLogin: RequestPostLogin
    ): Result<ResponsePostLogin> = loginService.postLogin(requestPostLogin)
}