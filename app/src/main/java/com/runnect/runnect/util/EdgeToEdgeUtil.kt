package com.runnect.runnect.util

import android.os.Build
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object EdgeToEdgeUtil {
    
    /**
     * Edge-to-Edge를 활성화하고 시스템바 insets을 처리합니다.
     * @param activity 대상 Activity
     * @param rootView insets을 적용할 루트 뷰
     * @param applyPadding 시스템바에 대한 패딩 적용 여부 (기본: true)
     */
    fun setupEdgeToEdge(
        activity: ComponentActivity,
        rootView: View,
        applyPadding: Boolean = true
    ) {
        // API 30+ (Android 11+)에서 Edge-to-Edge 활성화
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.enableEdgeToEdge()
        }
        
        // WindowInsets 처리
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            if (applyPadding) {
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            }
            
            insets
        }
    }
    
}