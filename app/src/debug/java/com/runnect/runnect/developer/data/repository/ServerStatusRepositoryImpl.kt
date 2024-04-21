package com.runnect.runnect.developer.data.repository

import com.runnect.runnect.data.network.FlowResult
import com.runnect.runnect.data.network.toEntity
import com.runnect.runnect.developer.data.source.remote.RemoteServerStatusDataSource
import com.runnect.runnect.developer.domain.ServerStatusRepository
import javax.inject.Inject

class ServerStatusRepositoryImpl @Inject constructor(
    private val serverStatusDataSource: RemoteServerStatusDataSource
) : ServerStatusRepository {

    override suspend fun checkServerStatus(serverUrl: String): FlowResult<String> {
        return serverStatusDataSource.checkServerStatus(serverUrl).toEntity {
            it.status
        }
    }
}