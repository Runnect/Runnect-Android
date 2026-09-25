package com.runnect.runnect.presentation.run

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.runnect.runnect.R

/**
 * 러닝 화면을 보고 있지 않을 때(백그라운드, 화면 꺼짐) 코스 이탈/페이스 저하 알림을 상단(헤드업) 알림으로 띄운다.
 * 진동은 RunAlertHaptics가 따로 울리므로 채널 자체 진동/소리는 끈다(패턴이 두 번 겹치지 않게).
 */
object RunAlertNotifier {
    private const val CHANNEL_ID = "run_alert"
    private const val NOTI_ID = 2

    // 마지막으로 게시한 알림. 같은 종류의 값만 바뀌면 조용히 내용만 갱신하고, 새 알림일 때만 다시 알린다.
    private var shown: RunAlert? = null

    fun update(context: Context, alert: RunAlert?, isUiVisible: Boolean, contentIntent: PendingIntent) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (alert == null || isUiVisible) {
            cancel(context)
            return
        }
        // 새 알림이면 기존 알림을 지우고 다시 게시해야 헤드업/알림음이 다시 뜬다(setOnlyAlertOnce는 떠 있는 알림의 갱신을 조용히 만든다).
        if (alert.isNewComparedTo(shown)) manager.cancel(NOTI_ID)
        shown = alert
        ensureChannel(context, manager)
        val (title, text) = when (alert) {
            is RunAlert.OffRoute -> context.getString(R.string.run_alert_off_route_title) to
                context.getString(R.string.run_alert_off_route_desc, alert.distanceM)
            is RunAlert.PaceDrop -> context.getString(R.string.run_alert_pace_drop_title) to
                context.getString(
                    R.string.run_alert_pace_drop_desc,
                    PaceFormat.format(alert.targetSecPerKm),
                    PaceFormat.format(alert.currentSecPerKm),
                )
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_run)
            .setColor(ContextCompat.getColor(context, R.color.M1))
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()
        manager.notify(NOTI_ID, notification)
    }

    fun cancel(context: Context) {
        shown = null
        context.getSystemService(NotificationManager::class.java).cancel(NOTI_ID)
    }

    private fun ensureChannel(context: Context, manager: NotificationManager) {
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.run_alert_channel_name),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                enableVibration(false)
                setSound(null, null)
            }
        )
    }
}
