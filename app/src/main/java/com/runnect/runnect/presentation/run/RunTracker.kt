package com.runnect.runnect.presentation.run

import com.naver.maps.geometry.LatLng
import com.runnect.runnect.util.extension.round
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** 러닝 화면에 그리는 한 시점의 러닝 상태. */
data class RunTrackingState(
    val elapsedSec: Int = 0,
    val distanceM: Double = 0.0, // 실시간 GPS 기반 이동 거리. 코스 목표 거리(고정값)와는 별개.
    val paceSecPerKm: Double? = null, // 최근 PACE_WINDOW 구간 기준. 충분히 움직이지 않았으면 null.
    val targetPaceSecPerKm: Double? = null,
    val alert: RunAlert? = null,
    val isPaused: Boolean = false,
) {
    /** 화면 표시용(소수 첫째 자리). */
    val distanceKm: Double get() = (distanceM / 1000).round(1)
}

/**
 * 러닝 한 번의 진행 상태(시간, 거리, 페이스, 자동 일시정지, 코스 이탈/페이스 저하 알림)를 계산한다.
 * 화면이 꺼지거나 앱이 백그라운드로 가도 계속 돌아야 하므로 러닝 포그라운드 서비스(TimerService)가 소유하고,
 * 화면은 state만 구독한다. Android 의존성이 없어 JVM 단위 테스트로 검증한다.
 */
class RunTracker {
    private val _state = MutableStateFlow(RunTrackingState())
    val state: StateFlow<RunTrackingState> = _state.asStateFlow()

    private var traveledDistanceM = 0.0
    private var lastLocation: LatLng? = null
    private var consecutiveJumpCount = 0
    private val recentSamples = ArrayDeque<Pair<Long, LatLng>>()

    private val alertEvaluator = RunAlertEvaluator()
    private var routePath: List<LatLng> = emptyList()

    private var lastMovementAtMillis: Long? = null
    // 움직임 판정 기준점. 업데이트 한 번의 이동량(1Hz면 러닝 중에도 2~4m)이 아니라 이 기준점에서 벗어난 거리로 판정해야
    // 천천히 달리는 중에 "안 움직임"으로 오판해 자동 일시정지되지 않는다.
    private var movementAnchor: LatLng? = null

    /** 코스 경로(출발점 + 경유 지점 순서)를 설정한다. 비어 있으면 이탈 판정을 하지 않는다. */
    fun setRoute(path: List<LatLng>) {
        routePath = path
    }

    /** 러닝 시작 전 고른 목표 페이스(초/km). null이면 페이스 저하 알림을 쓰지 않는다. */
    fun setTargetPace(paceSecPerKm: Double?) {
        _state.update { it.copy(targetPaceSecPerKm = paceSecPerKm?.takeIf { pace -> pace > 0.0 }) }
    }

    /**
     * 위치가 갱신될 때마다 호출한다. 일시정지 중에는 거리/페이스를 누적하지 않되, lastLocation은
     * 갱신해서 재개 시 일시정지 이전 위치와의 거리가 한 번에 더해지지 않게 한다.
     * GPS 튐으로 보이는 비정상적으로 큰 한 번의 이동(MAX_PLAUSIBLE_JUMP_M 초과)도 무시한다.
     */
    fun onLocationUpdated(newLocation: LatLng, now: Long = System.currentTimeMillis()) {
        val previous = lastLocation

        if (previous == null) {
            lastLocation = newLocation
            movementAnchor = newLocation
            lastMovementAtMillis = now // 무활동 타이머 시작 기준점 (러닝 시작 시점)
            return
        }
        if (_state.value.isPaused) {
            lastLocation = newLocation
            return
        }

        val deltaM = previous.distanceTo(newLocation)
        // 튐으로 판정해 거부한 좌표는 lastLocation을 갱신하지 않는다 — 다음 정상 좌표와의
        // 거리가 튄 좌표 기준으로 잘못 계산되는 것을 막기 위해.
        // 단, 기준점 자체가 틀렸던 경우(첫 측위가 부정확했던 경우 등) 이후 정상 좌표가 전부 거부되므로,
        // 튐이 연속으로 이어지면 기준점을 새 위치로 옮긴다(이 구간 거리는 누적하지 않음).
        if (deltaM > MAX_PLAUSIBLE_JUMP_M) {
            consecutiveJumpCount++
            if (consecutiveJumpCount >= MAX_CONSECUTIVE_JUMPS) {
                lastLocation = newLocation
                movementAnchor = newLocation
                recentSamples.clear() // 옮기기 전 위치가 페이스 구간에 섞이면 비현실적인 페이스(1km 3초 등)가 나온다
                consecutiveJumpCount = 0
            }
            return
        }
        consecutiveJumpCount = 0
        lastLocation = newLocation

        val anchor = movementAnchor ?: newLocation.also { movementAnchor = it }
        if (anchor.distanceTo(newLocation) >= MOVEMENT_THRESHOLD_M) {
            lastMovementAtMillis = now
            movementAnchor = newLocation
        }

        traveledDistanceM += deltaM
        recordPaceSample(now, newLocation)
        val pace = calculatePaceSecPerKm()
        val alert = alertEvaluator.evaluate(
            now = now,
            distanceToRouteM = RouteGeometry.distanceToPathM(newLocation, routePath),
            paceSecPerKm = pace,
            targetPaceSecPerKm = _state.value.targetPaceSecPerKm,
        )
        _state.update {
            it.copy(distanceM = traveledDistanceM, paceSecPerKm = pace, alert = alert)
        }
    }

    /** 1초마다 호출. 달리는 중이면 시간을 올리고, 무활동이 이어졌으면 자동 일시정지한다. 자동 일시정지했으면 true. */
    fun onSecondElapsed(now: Long): Boolean {
        if (_state.value.isPaused) return false
        _state.update { it.copy(elapsedSec = it.elapsedSec + 1) }
        if (!shouldAutoPause(now)) return false
        pause()
        return true
    }

    fun shouldAutoPause(now: Long): Boolean {
        if (_state.value.isPaused) return false
        val lastMovement = lastMovementAtMillis ?: return false
        return (now - lastMovement) >= INACTIVITY_TIMEOUT_MILLIS
    }

    /** 수동/자동 일시정지. 떠 있던 알림을 내리고 연속 판정을 처음부터 다시 시작하게 한다. */
    fun pause() {
        alertEvaluator.reset()
        recentSamples.clear() // 재개 후 페이스에 정지 중 이동분이 섞이지 않게 구간을 새로 시작한다
        _state.update { it.copy(isPaused = true, alert = null) } // 페이스 표시는 정지 직전 값을 그대로 둔다
    }

    /** 재개 — 무활동 타이머를 재개 시점 기준으로 초기화해서 재개 직후 바로 자동 일시정지되지 않게 한다. */
    fun resume(now: Long) {
        lastMovementAtMillis = now
        movementAnchor = lastLocation // 일시정지 중 이동분이 재개 직후 움직임으로 잡히지 않게
        _state.update { it.copy(isPaused = false) }
    }

    private fun recordPaceSample(now: Long, location: LatLng) {
        recentSamples.addLast(now to location)
        while (recentSamples.isNotEmpty() && now - recentSamples.first().first > PACE_WINDOW_MILLIS) {
            recentSamples.removeFirst()
        }
    }

    private fun calculatePaceSecPerKm(): Double? {
        if (recentSamples.size < 2) return null

        var windowDistanceM = 0.0
        for (i in 1 until recentSamples.size) {
            windowDistanceM += recentSamples[i - 1].second.distanceTo(recentSamples[i].second)
        }
        if (windowDistanceM < MIN_PACE_DISTANCE_M) return null

        val windowDurationSec = (recentSamples.last().first - recentSamples.first().first) / 1000.0
        if (windowDurationSec <= 0) return null

        return (windowDurationSec / windowDistanceM) * 1000
    }

    companion object {
        private const val MAX_PLAUSIBLE_JUMP_M = 50.0
        private const val MAX_CONSECUTIVE_JUMPS = 3
        private const val MOVEMENT_THRESHOLD_M = 10.0 // 제자리 GPS 흔들림(수 m)은 움직임으로 보지 않는다
        private const val PACE_WINDOW_MILLIS = 15_000L
        private const val MIN_PACE_DISTANCE_M = 5.0
        const val INACTIVITY_TIMEOUT_MILLIS = 20_000L
    }
}
