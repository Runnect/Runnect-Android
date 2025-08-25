package com.runnect.runnect.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object IntentUtil {
    
    /**
     * 안전한 PendingIntent를 생성합니다 (API 34+ 대응)
     * @param context 컨텍스트
     * @param requestCode 요청 코드
     * @param intent 대상 인텐트
     * @param flags PendingIntent 플래그
     * @param isMutable 가변 PendingIntent 여부 (기본: false)
     */
    fun createSafePendingIntent(
        context: Context,
        requestCode: Int,
        intent: Intent,
        flags: Int = 0,
        isMutable: Boolean = false
    ): PendingIntent {
        val finalFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags or if (isMutable) {
                PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_IMMUTABLE
            }
        } else {
            flags
        }
        
        return PendingIntent.getActivity(context, requestCode, intent, finalFlags)
    }
    
}