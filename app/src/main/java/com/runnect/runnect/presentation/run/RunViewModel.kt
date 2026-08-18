package com.runnect.runnect.presentation.run

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.util.extension.round

class RunViewModel : ViewModel() {
    var distanceSum = MutableLiveData(0.0)
    val departure = MutableLiveData<String>()
    val dataFrom = MutableLiveData<String>()
    var courseId = MutableLiveData<Int>()
    var publicCourseId = MutableLiveData<Int?>()
    val isPaused = MutableLiveData(false)

    // 실시간 GPS 기반 이동 거리 (km). distanceSum(코스 목표 거리, 고정값)과는 별개.
    val traveledDistanceKm = MutableLiveData(0.0)
    private var traveledDistanceM = 0.0
    private var lastLocation: LatLng? = null

    // 최근 구간(PACE_WINDOW_MILLIS) 거리/시간 기반 실시간 페이스. 너무 적게 움직였으면 null(표시 안 함).
    val currentPaceSecPerKm = MutableLiveData<Double?>(null)
    private val recentSamples = ArrayDeque<Pair<Long, LatLng>>()

    // 무활동 자동 일시정지 트리거 여부를 RunActivity가 매초(타이머 브로드캐스트) 확인한다.
    private var lastMovementAtMillis: Long? = null

    /**
     * 위치가 갱신될 때마다 호출한다. 일시정지 중에는 거리/페이스를 누적하지 않되, lastLocation은
     * 갱신해서 재개 시 일시정지 이전 위치와의 거리가 한 번에 더해지지 않게 한다.
     * GPS 튐으로 보이는 비정상적으로 큰 한 번의 이동(MAX_PLAUSIBLE_JUMP_M 초과)도 무시한다.
     */
    fun onLocationUpdated(newLocation: LatLng, now: Long = System.currentTimeMillis()) {
        val previous = lastLocation
        lastLocation = newLocation

        if (previous == null) {
            lastMovementAtMillis = now // 무활동 타이머 시작 기준점 (러닝 시작 시점)
            return
        }
        if (isPaused.value == true) return

        val deltaM = previous.distanceTo(newLocation)
        if (deltaM > MAX_PLAUSIBLE_JUMP_M) return

        if (deltaM >= MOVEMENT_THRESHOLD_M) {
            lastMovementAtMillis = now
        }

        traveledDistanceM += deltaM
        traveledDistanceKm.value = (traveledDistanceM / 1000).round(1)

        recordPaceSample(now, newLocation)
    }

    /** 수동으로 재개했을 때 호출 — 무활동 타이머를 재개 시점 기준으로 초기화해서 재개 직후 바로 자동 일시정지되지 않게 한다. */
    fun onManualResume(now: Long = System.currentTimeMillis()) {
        lastMovementAtMillis = now
    }

    /** 타이머 브로드캐스트(매초)에서 호출. 무활동 시간이 임계값을 넘었으면 true — RunActivity가 실제 일시정지를 수행한다. */
    fun shouldAutoPause(now: Long = System.currentTimeMillis()): Boolean {
        if (isPaused.value == true) return false
        val lastMovement = lastMovementAtMillis ?: return false
        return (now - lastMovement) >= INACTIVITY_TIMEOUT_MILLIS
    }

    private fun recordPaceSample(now: Long, location: LatLng) {
        recentSamples.addLast(now to location)
        while (recentSamples.isNotEmpty() && now - recentSamples.first().first > PACE_WINDOW_MILLIS) {
            recentSamples.removeFirst()
        }
        currentPaceSecPerKm.value = calculatePaceSecPerKm()
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
        private const val MOVEMENT_THRESHOLD_M = 5.0
        private const val PACE_WINDOW_MILLIS = 15_000L
        private const val MIN_PACE_DISTANCE_M = 5.0
        private const val INACTIVITY_TIMEOUT_MILLIS = 60_000L
    }
}
