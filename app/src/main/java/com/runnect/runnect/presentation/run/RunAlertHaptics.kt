package com.runnect.runnect.presentation.run

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * 러닝 중 알림이 새로 뜰 때 진동으로도 알린다. 화면을 보지 않고 달리는 상황이 대부분이라 배너만으로는 놓치기 쉽다.
 * 같은 알림이 유지되는 동안(거리/페이스 값만 갱신)에는 다시 울리지 않는다.
 */
class RunAlertHaptics(context: Context, initialAlert: RunAlert?) {
    private val vibrator: Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    // 화면 재생성 직후 이미 떠 있던 알림으로 다시 울리지 않도록 현재 알림에서 시작한다.
    private var previous: RunAlert? = initialAlert

    fun onAlertChanged(alert: RunAlert?) {
        val pattern = patternFor(previous, alert)
        previous = alert
        if (pattern == null || vibrator?.hasVibrator() != true) return
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, NO_REPEAT))
    }

    companion object {
        private const val NO_REPEAT = -1

        // 코스 이탈은 바로 행동(복귀)이 필요해 두 번, 페이스 저하는 한 번 길게 울려 손목/주머니에서도 구분되게 한다.
        val OFF_ROUTE_PATTERN = longArrayOf(0, 300, 150, 300)
        val PACE_DROP_PATTERN = longArrayOf(0, 500)

        /** 알림이 없던 상태에서 새로 떴거나 다른 종류로 바뀌었을 때만 진동 패턴을 반환한다. */
        fun patternFor(previous: RunAlert?, current: RunAlert?): LongArray? {
            if (current == null || (previous != null && previous::class == current::class)) return null
            return when (current) {
                is RunAlert.OffRoute -> OFF_ROUTE_PATTERN
                is RunAlert.PaceDrop -> PACE_DROP_PATTERN
            }
        }
    }
}
