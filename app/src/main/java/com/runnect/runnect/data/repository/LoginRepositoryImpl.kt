package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.request.RequestPostLogin
import com.runnect.runnect.data.dto.LoginDTO
import com.runnect.runnect.data.source.remote.RemoteLoginDataSource
import com.runnect.runnect.domain.LoginRepository
import com.runnect.runnect.util.extension.toData
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val remoteLoginDataSource: RemoteLoginDataSource) : LoginRepository {
    override suspend fun postLogin(requestPostLogin: RequestPostLogin): LoginDTO {
        return remoteLoginDataSource.postLogin(requestPostLogin).toData()
    }
}