package com.runnect.runnect.presentation.run

import kotlin.math.roundToInt

object PaceFormat {
    /** 초/km 페이스를 5'48" 형식으로 표시. 값이 없거나 비정상이면 "-". */
    fun format(paceSecPerKm: Double?): String {
        if (paceSecPerKm == null || paceSecPerKm.isNaN() || paceSecPerKm.isInfinite()) return "-"
        val totalSec = paceSecPerKm.roundToInt()
        return "%d'%02d\"".format(totalSec / 60, totalSec % 60)
    }

    /**
     * 서버 기록의 소요 시간 문자열("HH:MM:SS" 또는 "MM:SS")을 초로 변환. 형식이 맞지 않으면 null.
     * 서버의 pace 문자열은 EndRunActivity가 분 단위 소수를 초처럼 이어 붙여 저장해 신뢰할 수 없으므로,
     * 페이스가 필요하면 이 값과 코스 거리로 직접 계산한다.
     */
    fun parseDurationSec(time: String?): Int? {
        val parts = time?.trim()?.split(":")?.takeIf { it.size in 2..3 } ?: return null
        val numbers = parts.map { it.toIntOrNull()?.takeIf { n -> n >= 0 } ?: return null }
        return numbers.fold(0) { acc, n -> acc * 60 + n }
    }

    /** 소요 시간과 거리로 초/km 페이스 계산. 계산 불가하면 null. */
    fun paceFromDuration(time: String?, distanceKm: Double?): Double? {
        val durationSec = parseDurationSec(time) ?: return null
        if (distanceKm == null || distanceKm <= 0.0 || durationSec <= 0) return null
        return durationSec / distanceKm
    }
}
