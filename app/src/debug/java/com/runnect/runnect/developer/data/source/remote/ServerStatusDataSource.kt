package com.runnect.runnect.developer.data.source.remote

import com.runnect.runnect.data.network.FlowResult
import com.runnect.runnect.developer.data.dto.ResponseServerStatus
import com.runnect.runnect.developer.data.service.ServerStatusService
import javax.inject.Inject

class ServerStatusDataSource @Inject constructor(
    private val serverStatusService: ServerStatusService,
) {

    fun checkServerStatus(serverUrl: String): FlowResult<ResponseServerStatus> {
        return serverStatusService.checkServerStatus(serverUrl)
    }
}