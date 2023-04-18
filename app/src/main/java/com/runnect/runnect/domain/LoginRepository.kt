package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.request.RequestLogin
import com.runnect.runnect.data.dto.response.LoginDTO

interface LoginRepository {
    suspend fun postLogin(requestLogin: RequestLogin): LoginDTO
}