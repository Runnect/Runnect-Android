package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.request.RequestPostLogin
import com.runnect.runnect.data.dto.LoginDTO
import com.runnect.runnect.data.network.mapToFlowResult
import com.runnect.runnect.data.source.remote.RemoteLoginDataSource
import com.runnect.runnect.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val remoteLoginDataSource: RemoteLoginDataSource) :
    LoginRepository {

    override suspend fun postLogin(requestPostLogin: RequestPostLogin): Flow<Result<LoginDTO>> {
        return remoteLoginDataSource.postLogin(requestPostLogin).mapToFlowResult {
            it.toData()
        }
    }
}