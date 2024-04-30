package com.runnect.runnect.developer.enum

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.runnect.runnect.R
import com.runnect.runnect.developer.presentation.RunnectDeveloperViewModel.ServerState

enum class ServerStatus(
    @ColorRes val colorRes: Int,
    @StringRes val statusRes: Int,
    @StringRes val summaryRes: Int,
) {

    CHECKING(R.color.blue, R.string.developer_server_status_checking_title, R.string.developer_server_status_checking_sub),
    RUNNING(R.color.green, R.string.developer_server_status_running_title, R.string.developer_server_status_running_sub),
    DEGRADED(R.color.orange, R.string.developer_server_status_degraded_title, R.string.developer_server_status_degraded_sub),
    ERROR(R.color.red, R.string.developer_server_status_error_title, R.string.developer_server_status_error_sub),
    UNKNOWN(R.color.grey, R.string.developer_server_status_unknown_title, R.string.developer_server_status_unknown_sub);

    companion object {
        fun getStatus(state: ServerState): ServerStatus {
            return when (state) {
                ServerState.Running -> RUNNING
                ServerState.Degraded -> DEGRADED
                ServerState.Error -> ERROR
                ServerState.Unknown -> UNKNOWN
                ServerState.Checking -> CHECKING
            }
        }
    }
}