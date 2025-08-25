package com.runnect.runnect.util

import android.app.Service
import android.content.pm.ServiceInfo
import android.os.Build

object ForegroundServiceUtil {
    
    
    /**
     * Service에서 startForeground 호출 시 사용
     */
    fun startForegroundSafely(
        service: Service,
        notificationId: Int,
        notification: android.app.Notification,
        foregroundServiceType: Int = ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // API 29+ foregroundServiceType 지정
                service.startForeground(notificationId, notification, foregroundServiceType)
            } else {
                service.startForeground(notificationId, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
}