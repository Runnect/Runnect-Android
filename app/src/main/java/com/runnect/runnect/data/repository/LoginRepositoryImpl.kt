package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.request.RequestLogin
import com.runnect.runnect.data.dto.response.LoginDTO
import com.runnect.runnect.data.source.remote.RemoteLoginDataSource
import com.runnect.runnect.domain.LoginRepository
import com.runnect.runnect.util.extension.toData
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val remoteLoginDataSource: RemoteLoginDataSource) : LoginRepository {
    override suspend fun postLogin(requestLogin: RequestLogin): LoginDTO {
        return remoteLoginDataSource.postLogin(requestLogin).toData()
    }
}