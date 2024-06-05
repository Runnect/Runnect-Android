package com.runnect.runnect.developer.data.repository

import com.runnect.runnect.data.network.FlowResult
import com.runnect.runnect.data.network.toEntityResult
import com.runnect.runnect.developer.data.source.remote.ServerStatusDataSource
import com.runnect.runnect.developer.domain.ServerStatusRepository
import javax.inject.Inject

class ServerStatusRepositoryImpl @Inject constructor(
    private val serverStatusDataSource: ServerStatusDataSource
) : ServerStatusRepository {

    override suspend fun checkServerStatus(serverUrl: String): FlowResult<String> {
        return serverStatusDataSource.checkServerStatus(serverUrl).toEntityResult {
            it.status
        }
    }
}