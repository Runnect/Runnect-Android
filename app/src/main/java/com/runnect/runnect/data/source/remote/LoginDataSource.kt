package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.api.LoginService
import com.runnect.runnect.data.dto.request.RequestLogin
import com.runnect.runnect.data.dto.response.ResponseLogin
import javax.inject.Inject

class LoginDataSource @Inject constructor(private val loginService: LoginService) {
    suspend fun postLogin(requestLoginDto: RequestLogin):ResponseLogin=loginService.postLogin(requestLoginDto)
}