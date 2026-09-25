package com.runnect.runnect.presentation.run

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sqrt

/**
 * 코스 경로를 따라 1초마다 가짜 위치를 FusedLocationProvider에 주입한다(debug 전용).
 * 앱 전체 위치 파이프라인(네이버 지도 FusedLocationSource → RunViewModel)을 그대로 타기 때문에 실제로 뛰는 것과 같은 경로로 검증된다.
 * 사용하려면 이 앱이 모의 위치 앱으로 허용돼 있어야 한다: adb shell appops set com.runnect.runnect android:mock_location allow
 */
class GpsRouteSimulator(context: Context, val route: List<LatLng>) {

    enum class Speed(val label: String, val metersPerSec: Double) {
        SLOW("느림 7'35\"", 2.2),
        NORMAL("보통 5'57\"", 2.8),
        FAST("빠름 4'46\"", 3.5),
    }

    // 화면 회전으로 Activity가 재생성돼도 시뮬레이션이 끊기지 않도록 Activity 수명과 무관한 scope에서 돈다.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context.applicationContext)
    private val path = route.zipWithNext().filter { (a, b) -> a.distanceTo(b) > 0.0 }
    private val totalLengthM = path.sumOf { (a, b) -> a.distanceTo(b) }

    private var job: Job? = null
    private var traveledM = 0.0

    var speed = Speed.NORMAL
    var offRouteM = 0.0 // 0이면 코스 위, 양수면 그만큼 코스 옆으로 벗어나서 달린다
        set(value) {
            field = value
            if (value > 0.0) offsetSide = pickOffsetSide(value)
        }
    // 벗어날 방향(1=진행 방향 왼쪽, -1=오른쪽). 코스가 꺾이는 곳에서 안쪽으로 벗어나면 다른 구간과 가까워져
    // 실제로는 코스 근처인 위치가 되므로, 앞으로 달릴 구간 기준으로 코스 전체에서 더 멀어지는 쪽을 고른다.
    private var offsetSide = 1.0
    // 실제 사람처럼 코스에서 점점 멀어지고/돌아오도록 1초에 OFFSET_STEP_M씩만 목표 이탈 거리로 옮겨간다.
    // (한 번에 50m를 옮기면 앱의 GPS 튐 필터에 걸려 실제와 다른 경로로 검증된다)
    private var currentOffsetM = 0.0
    var isHalted = false // true면 제자리에 서 있는다(자동 일시정지 확인용)
    val isRunning get() = job?.isActive == true

    var onError: (Throwable) -> Unit = {}

    fun start() {
        if (isRunning || path.isEmpty()) return
        fusedClient.setMockMode(true)
            .addOnSuccessListener {
                job = scope.launch {
                    while (isActive) {
                        if (!isHalted) {
                            traveledM = (traveledM + speed.metersPerSec).coerceAtMost(totalLengthM)
                            val targetOffset = offRouteM * offsetSide
                            currentOffsetM += (targetOffset - currentOffsetM).coerceIn(-OFFSET_STEP_M, OFFSET_STEP_M)
                        }
                        pushLocation(positionAt(traveledM))
                        delay(TICK_MILLIS)
                    }
                }
            }
            .addOnFailureListener(onError)
    }

    /** 코스 출발점으로 되돌린다(TC를 같은 조건에서 다시 시작할 때). */
    fun rewind() {
        traveledM = 0.0
        currentOffsetM = 0.0
        offRouteM = 0.0
    }

    fun stop() {
        job?.cancel()
        job = null
        runCatching { fusedClient.setMockMode(false) }
    }

    private fun positionAt(distanceM: Double): LatLng {
        var remaining = distanceM
        val (a, b) = path.firstOrNull { (a, b) ->
            val length = a.distanceTo(b)
            (remaining <= length).also { if (!it) remaining -= length }
        } ?: return path.last().second.let { offset(path.last().first, it, it) }

        val t = remaining / a.distanceTo(b)
        val point = LatLng(
            a.latitude + (b.latitude - a.latitude) * t,
            a.longitude + (b.longitude - a.longitude) * t,
        )
        return offset(a, b, point)
    }

    private fun pickOffsetSide(meters: Double): Double {
        val lookahead = (0..LOOKAHEAD_SEC step 2).map { traveledM + speed.metersPerSec * it }
        fun worstCase(side: Double) = lookahead.minOf { d ->
            val point = offsetAt(d, meters * side)
            RouteGeometry.distanceToPathM(point, route) ?: 0.0
        }
        return if (worstCase(1.0) >= worstCase(-1.0)) 1.0 else -1.0
    }

    /** 경로상 distanceM 지점에서 진행 방향 왼쪽으로 signedOffsetM(음수면 오른쪽)만큼 떨어진 위치. */
    private fun offsetAt(distanceM: Double, signedOffsetM: Double): LatLng {
        val saved = currentOffsetM
        currentOffsetM = signedOffsetM
        return positionAt(distanceM).also { currentOffsetM = saved }
    }

    /** 진행 방향(a→b)의 법선 방향으로 currentOffsetM만큼 이동시킨다. */
    private fun offset(a: LatLng, b: LatLng, point: LatLng): LatLng {
        if (currentOffsetM == 0.0) return point
        val metersPerDegLat = 111_320.0
        val metersPerDegLng = metersPerDegLat * cos(Math.toRadians(point.latitude))
        val dx = (b.longitude - a.longitude) * metersPerDegLng
        val dy = (b.latitude - a.latitude) * metersPerDegLat
        val length = sqrt(dx * dx + dy * dy).takeIf { it > 0.0 } ?: return point
        val nx = -dy / length
        val ny = dx / length
        return LatLng(
            point.latitude + ny * currentOffsetM / metersPerDegLat,
            point.longitude + nx * currentOffsetM / metersPerDegLng,
        )
    }

    private fun pushLocation(latLng: LatLng) {
        val location = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = latLng.latitude
            longitude = latLng.longitude
            accuracy = 5f
            speed = if (isHalted) 0f else this@GpsRouteSimulator.speed.metersPerSec.toFloat()
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }
        fusedClient.setMockLocation(location).addOnFailureListener(onError)
    }

    companion object {
        private const val TICK_MILLIS = 1_000L
        private const val OFFSET_STEP_M = 10.0
        private const val LOOKAHEAD_SEC = 30
    }
}
