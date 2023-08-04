package com.runnect.runnect.presentation.run

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.runnect.runnect.data.dto.TimerData
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

class TimerService : Service() {

    private val binder = LocalBinder()
    private var timer: Timer? = null
    private var time = 0

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    // 서비스 시작 시 호출
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTimer()
        return START_STICKY
    }

    fun startTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                time++

                val hour = time / 3600
                val minute = time / 60
                val second = time % 60

                val timerValue = String.format("%02d:%02d:%02d", hour, minute, second)
                Timber.tag("Timer").d(timerValue)

                // 결과를 Activity로 전달
                val intent = Intent(TIMER_UPDATE_ACTION)
                intent.putExtra(
                    EXTRA_TIMER_VALUE, TimerData(
                        hour = hour,
                        minute = minute,
                        second = second
                    )
                )
                sendBroadcast(intent)
            }
        }, 0, 1000)
    }

    fun stopTimer() {
        timer?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    companion object {
        const val TIMER_UPDATE_ACTION = "TIMER_UPDATE_ACTION"
        const val EXTRA_TIMER_VALUE = "EXTRA_TIMER_VALUE"
    }
}