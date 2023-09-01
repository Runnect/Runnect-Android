package com.runnect.runnect.presentation.run

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.TimerData
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

class TimerService : Service() {

    private val binder = LocalBinder()
    private var timer: Timer? = null
    private var time = 0
    private var player: MediaPlayer? = null
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val NOTI_ID = 1 // 알림 ID

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    // 서비스 시작 시 호출
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notifyStartRun()
        startTimer()
        initNotification()
        return START_STICKY
    }

    fun startTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                time++

                val hour = time / 3600
                val minute = (time % 3600) / 60
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

                // 알림의 내용 업데이트
                notificationBuilder.setContentText(timerValue)
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTI_ID, notificationBuilder.build())
            }
        }, 0, 1000)
    }

    fun stopTimer() {
        timer?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun initNotification() {
        notificationBuilder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.drawable.runnect_logo_circle)
            .setContentTitle("러닝")
            .setContentText("00:00:00")
            .setOngoing(true) // true 일 경우 알림 리스트에서 클릭하거나 좌우로 드래그해도 사라지지 않음

        val notificationIntent = Intent(this@TimerService, RunActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this@TimerService,
            0,
            notificationIntent,
            FLAG_IMMUTABLE
        )
        notificationBuilder.setContentIntent(pendingIntent) // 알림 클릭 시 이동

        // 알림 표시
        val notificationManager =
            this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "default",
                    "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        notificationManager.notify(NOTI_ID, notificationBuilder.build()) // id : 정의해야하는 각 알림의 고유한 int값
        val notification = notificationBuilder.build()
        startForeground(NOTI_ID, notification)
    }

    fun notifyStartRun() {
        if (player == null) {
            player = MediaPlayer.create(this@TimerService, R.raw.start_run)
            player?.setOnCompletionListener { mediaPlayer ->
                mediaPlayer.release() // 재생이 끝나면 MediaPlayer 객체를 해제합니다.
            }
        }
        player?.start()
    }

    companion object {
        const val TIMER_UPDATE_ACTION = "TIMER_UPDATE_ACTION"
        const val EXTRA_TIMER_VALUE = "EXTRA_TIMER_VALUE"
    }
}
