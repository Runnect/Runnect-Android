package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.service.LoginService
import com.runnect.runnect.data.dto.request.RequestPostLogin
import com.runnect.runnect.data.dto.response.ResponseLogin
import javax.inject.Inject

class RemoteLoginDataSource @Inject constructor(private val loginService: LoginService) {
    suspend fun postLogin(requestPostLogin: RequestPostLogin):ResponseLogin=loginService.postLogin(requestPostLogin)
}