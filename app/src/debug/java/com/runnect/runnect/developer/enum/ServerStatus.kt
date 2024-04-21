package com.runnect.runnect.developer.enum

import android.content.Context
import com.runnect.runnect.R
import com.runnect.runnect.developer.presentation.RunnectDeveloperViewModel.ServerState

enum class ServerStatus(
    private val colorRes: Int,
    val statusText: String,
    val summary: String,
) {
    CHECKING(R.color.blue, "Checking...", "서버 상태를 확인 중입니다."),
    RUNNING(R.color.green, "Running", "서버가 정상적으로 작동하고 있습니다."),
    DEGRADED(R.color.orange, "Degraded", "작동하고 있으나 일부 기능에 문제가 있습니다."),
    NOT_RUNNING(R.color.red, "Not Running", "작동 중이지 않거나 오류가 있습니다.");

    fun getColor(context: Context): Int = context.getColor(colorRes)

    companion object {
        fun getStatus(state: ServerState): ServerStatus {
            return when (state) {
                ServerState.Running -> RUNNING
                ServerState.Degraded -> DEGRADED
                ServerState.NotRunning -> NOT_RUNNING
                ServerState.Checking -> CHECKING
            }
        }
    }
}