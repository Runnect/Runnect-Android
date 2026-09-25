package com.runnect.runnect.presentation.run

import com.naver.maps.geometry.LatLng
import kotlin.math.cos
import kotlin.math.sqrt

/** 러닝 중 화면 상단에 띄우는 알림. 동시에 하나만 노출하며, 코스 이탈이 페이스 저하보다 우선한다. */
sealed interface RunAlert {
    data class OffRoute(val distanceM: Int) : RunAlert
    data class PaceDrop(val targetSecPerKm: Double, val currentSecPerKm: Double) : RunAlert
}

object RouteGeometry {
    private const val EARTH_RADIUS_M = 6_371_000.0

    /**
     * 현재 좌표에서 경로(선분 목록)까지의 최단 거리(m).
     * 러닝 코스 규모(수 km)에서는 현재 좌표 기준 등장방형 투영으로 평면 근사해도 오차가 무시할 수준이다.
     */
    fun distanceToPathM(point: LatLng, path: List<LatLng>): Double? {
        if (path.isEmpty()) return null
        if (path.size == 1) return point.distanceTo(path.first())

        val metersPerDegLat = Math.toRadians(1.0) * EARTH_RADIUS_M
        val metersPerDegLng = metersPerDegLat * cos(Math.toRadians(point.latitude))
        fun project(p: LatLng) = Pair(
            (p.longitude - point.longitude) * metersPerDegLng,
            (p.latitude - point.latitude) * metersPerDegLat,
        )

        return path.zipWithNext().minOf { (a, b) ->
            val (ax, ay) = project(a)
            val (bx, by) = project(b)
            distanceFromOriginToSegment(ax, ay, bx, by)
        }
    }

    private fun distanceFromOriginToSegment(ax: Double, ay: Double, bx: Double, by: Double): Double {
        val dx = bx - ax
        val dy = by - ay
        val lengthSq = dx * dx + dy * dy
        val t = if (lengthSq == 0.0) 0.0 else (-(ax * dx + ay * dy) / lengthSq).coerceIn(0.0, 1.0)
        val x = ax + t * dx
        val y = ay + t * dy
        return sqrt(x * x + y * y)
    }
}

/**
 * 코스 이탈 / 페이스 저하 알림 판정. GPS 순간 튐으로 인한 오탐을 막기 위해 조건이 일정 시간 "연속" 유지될 때만 알림을 띄우고,
 * 조건이 해소되면 바로 내린다. 같은 종류의 알림은 마지막으로 띄운 시점부터 쿨다운 동안 다시 띄우지 않는다.
 */
class RunAlertEvaluator {
    private var offRouteSinceMillis: Long? = null
    private var paceDropSinceMillis: Long? = null
    private var lastOffRouteShownAtMillis: Long? = null
    private var lastPaceDropShownAtMillis: Long? = null
    private var current: RunAlert? = null

    fun evaluate(
        now: Long,
        distanceToRouteM: Double?,
        paceSecPerKm: Double?,
        targetPaceSecPerKm: Double?,
    ): RunAlert? {
        val offRoute = evaluateOffRoute(now, distanceToRouteM)
        // 연속 판정 타이머는 항상 갱신하되, 이탈 알림이 떠 있으면 페이스 알림은 띄우지 않는다(쿨다운도 소모하지 않음).
        val paceDropCandidate = updatePaceDropTimer(now, paceSecPerKm, targetPaceSecPerKm)
        current = offRoute ?: paceDropCandidate?.let { showPaceDrop(now, it) }
        return current
    }

    /** 일시정지 시 호출 — 진행 중이던 연속 판정과 떠 있던 알림을 초기화한다. 쿨다운 기록은 유지한다. */
    fun reset() {
        offRouteSinceMillis = null
        paceDropSinceMillis = null
        current = null
    }

    private fun evaluateOffRoute(now: Long, distanceToRouteM: Double?): RunAlert.OffRoute? {
        if (distanceToRouteM == null || distanceToRouteM <= OFF_ROUTE_THRESHOLD_M) {
            offRouteSinceMillis = null
            return null
        }
        val since = offRouteSinceMillis ?: now.also { offRouteSinceMillis = it }
        if (now - since < OFF_ROUTE_DURATION_MILLIS) return null

        val alert = RunAlert.OffRoute(distanceToRouteM.toInt())
        if (current is RunAlert.OffRoute) return alert
        if (isCoolingDown(now, lastOffRouteShownAtMillis)) return null
        lastOffRouteShownAtMillis = now
        return alert
    }

    /** 페이스 저하가 임계 시간 이상 연속됐으면 띄울 알림 후보를, 아니면 null을 반환한다. */
    private fun updatePaceDropTimer(now: Long, paceSecPerKm: Double?, targetPaceSecPerKm: Double?): RunAlert.PaceDrop? {
        if (paceSecPerKm == null || targetPaceSecPerKm == null ||
            paceSecPerKm < targetPaceSecPerKm * PACE_DROP_RATIO
        ) {
            paceDropSinceMillis = null
            return null
        }
        val since = paceDropSinceMillis ?: now.also { paceDropSinceMillis = it }
        if (now - since < PACE_DROP_DURATION_MILLIS) return null
        return RunAlert.PaceDrop(targetPaceSecPerKm, paceSecPerKm)
    }

    private fun showPaceDrop(now: Long, alert: RunAlert.PaceDrop): RunAlert.PaceDrop? {
        if (current is RunAlert.PaceDrop) return alert
        if (isCoolingDown(now, lastPaceDropShownAtMillis)) return null
        lastPaceDropShownAtMillis = now
        return alert
    }

    private fun isCoolingDown(now: Long, lastShownAt: Long?): Boolean =
        lastShownAt != null && now - lastShownAt < ALERT_COOLDOWN_MILLIS

    companion object {
        // 도심 GPS 오차가 보통 5~15m라 여유를 둔 초기값. 실측 후 조정 대상.
        const val OFF_ROUTE_THRESHOLD_M = 30.0
        const val OFF_ROUTE_DURATION_MILLIS = 8_000L
        const val PACE_DROP_RATIO = 1.2 // 목표보다 20% 이상 느림 (페이스는 값이 클수록 느림)
        const val PACE_DROP_DURATION_MILLIS = 10_000L
        const val ALERT_COOLDOWN_MILLIS = 60_000L
    }
}
