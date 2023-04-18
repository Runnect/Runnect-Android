package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.request.RequestLogin
import com.runnect.runnect.data.dto.response.LoginDTO
import com.runnect.runnect.data.source.remote.LoginDataSource
import com.runnect.runnect.domain.LoginRepository
import com.runnect.runnect.util.extension.toData

class LoginRepositoryImpl(private val loginDataSource: LoginDataSource) : LoginRepository {
    override suspend fun postLogin(requestLogin: RequestLogin): LoginDTO {
        return loginDataSource.postLogin(requestLogin).toData()
    }
}