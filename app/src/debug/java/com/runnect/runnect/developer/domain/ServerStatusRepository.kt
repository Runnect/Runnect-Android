package com.runnect.runnect.developer.domain

import com.runnect.runnect.data.network.FlowResult

interface ServerStatusRepository {

    suspend fun checkServerStatus(serverUrl: String): FlowResult<String>
}