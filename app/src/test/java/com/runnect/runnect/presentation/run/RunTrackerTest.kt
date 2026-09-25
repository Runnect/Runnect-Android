package com.runnect.runnect.presentation.run

import com.naver.maps.geometry.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RunTrackerTest {

    private lateinit var tracker: RunTracker

    @Before
    fun setUp() {
        tracker = RunTracker()
    }

    @Test
    fun `첫 위치 업데이트는 이전 위치가 없어서 거리를 누적하지 않는다`() {
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780))

        assertEquals(0.0, tracker.state.value.distanceKm, 0.0)
    }

    @Test
    fun `연속된 여러 위치 이동만큼 거리가 누적된다`() {
        val baseLat = 37.5665
        tracker.onLocationUpdated(LatLng(baseLat, 126.9780))
        // 각 구간 약 22m씩(임계값 50m 이내), 5구간 합산 약 111m
        repeat(5) { i ->
            tracker.onLocationUpdated(LatLng(baseLat + 0.0002 * (i + 1), 126.9780))
        }

        val distanceKm = tracker.state.value.distanceKm ?: 0.0
        assertTrue("누적 거리가 0보다 커야 한다: $distanceKm", distanceKm > 0.0)
        assertTrue("누적 거리가 비정상적으로 크면 안 된다: $distanceKm", distanceKm < 0.3)
    }

    @Test
    fun `일시정지 중에는 이동해도 거리가 누적되지 않는다`() {
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780))
        tracker.pause()

        tracker.onLocationUpdated(LatLng(37.5675, 126.9780))

        assertEquals(0.0, tracker.state.value.distanceKm, 0.0)
    }

    @Test
    fun `일시정지 해제 후에는 정지 중 이동분을 제외하고 재개 이후 이동만 누적된다`() {
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780))
        tracker.pause()
        tracker.onLocationUpdated(LatLng(37.5715, 126.9780)) // 일시정지 중 약 555m 이동 — 누적되면 안 됨
        tracker.resume(0L)

        tracker.onLocationUpdated(LatLng(37.5716, 126.9780)) // 재개 후 약 11m만 실제 이동

        val distanceAfterResume = tracker.state.value.distanceKm ?: 0.0
        assertTrue(
            "재개 직후 거리는 일시정지 중 이동분(약 555m)을 포함하면 안 된다: $distanceAfterResume",
            distanceAfterResume < 0.1
        )
    }

    @Test
    fun `GPS 튐으로 보이는 비정상적으로 큰 한 번의 이동은 무시한다`() {
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780))
        tracker.onLocationUpdated(LatLng(37.5765, 126.9780)) // 약 1.1km, 임계값(50m) 훨씬 초과

        assertEquals(0.0, tracker.state.value.distanceKm, 0.0)
    }

    @Test
    fun `거부된 튐 좌표는 기준점으로 남지 않고 다음 정상 이동은 튐 이전 위치 기준으로 계산된다`() {
        val baseLat = 37.5665
        val now = 0L
        tracker.onLocationUpdated(LatLng(baseLat, 126.9780), now) // 기준점
        // 약 122m 튐 - 거부됨. 이 좌표가 lastLocation으로 남으면 이후 정상 이동까지 잘못 거부/누적된다.
        tracker.onLocationUpdated(LatLng(baseLat + 0.0011, 126.9780), now + 1_000)
        // 튐 이전 기준점(baseLat)으로부터 약 44m - 정상 이동이면 누적되어야 한다.
        tracker.onLocationUpdated(LatLng(baseLat + 0.0004, 126.9780), now + 2_000)
        // 직전 정상 위치로부터 약 44m 추가 이동
        tracker.onLocationUpdated(LatLng(baseLat + 0.0008, 126.9780), now + 3_000)

        val distanceKm = tracker.state.value.distanceKm ?: 0.0
        assertTrue(
            "튐 좌표가 기준점으로 남으면 이후 정상 이동(총 약 89m)이 누락된다: $distanceKm",
            distanceKm >= 0.1
        )
    }

    @Test
    fun `충분히 움직이지 않으면 페이스는 null이다`() {
        val now = 0L
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780), now)

        assertEquals(null, tracker.state.value.paceSecPerKm)
    }

    @Test
    fun `최근 구간 이동 거리와 시간으로 페이스가 계산된다`() {
        val now = 0L
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780), now)
        // 페이스 샘플은 두 번째 위치 업데이트부터 기록되므로, 최소 2개 샘플을 쌓기 위해 3번 갱신한다.
        tracker.onLocationUpdated(LatLng(37.5667, 126.9780), now + 5_000)
        tracker.onLocationUpdated(LatLng(37.5669, 126.9780), now + 10_000)

        val pace = tracker.state.value.paceSecPerKm
        assertTrue("페이스가 계산되어야 한다: $pace", pace != null && pace > 0)
    }

    @Test
    fun `일시정지 중에는 페이스가 갱신되지 않는다`() {
        val now = 0L
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780), now)
        tracker.pause()

        tracker.onLocationUpdated(LatLng(37.5675, 126.9780), now + 10_000)

        assertEquals(null, tracker.state.value.paceSecPerKm)
    }

    @Test
    fun `무활동 시간이 임계값을 넘으면 자동 일시정지가 필요하다`() {
        val now = 0L
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780), now)

        assertTrue(tracker.shouldAutoPause(now + 20_000))
    }

    @Test
    fun `무활동 시간이 임계값 미만이면 자동 일시정지가 필요없다`() {
        val now = 0L
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780), now)

        assertEquals(false, tracker.shouldAutoPause(now + 19_000))
    }

    @Test
    fun `이미 일시정지 상태면 자동 일시정지를 다시 트리거하지 않는다`() {
        val now = 0L
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780), now)
        tracker.pause()

        assertEquals(false, tracker.shouldAutoPause(now + 20_000))
    }

    @Test
    fun `수동 재개 직후에는 무활동 타이머가 초기화되어 바로 자동 일시정지되지 않는다`() {
        val now = 0L
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780), now)

        tracker.resume(now + 60_000)

        assertEquals(false, tracker.shouldAutoPause(now + 60_000 + 19_000))
    }

    @Test
    fun `움직임이 감지되면 무활동 타이머가 갱신된다`() {
        val now = 0L
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780), now)
        // 임계값(10m) 이상 이동(약 11m) -> 무활동 타이머 갱신
        tracker.onLocationUpdated(LatLng(37.5666, 126.9780), now + 30_000)

        assertEquals(false, tracker.shouldAutoPause(now + 30_000 + 19_000))
        assertTrue(tracker.shouldAutoPause(now + 30_000 + 20_000))
    }

    // 동서 방향 약 350m 직선 코스
    private val straightRoute = listOf(LatLng(37.5665, 126.9780), LatLng(37.5665, 126.9820))

    /** 코스와 평행하게 북쪽으로 latOffset만큼 떨어져 1초에 약 18m씩 동쪽으로 달린다. */
    private fun runAlongside(latOffset: Double, seconds: Int, startMillis: Long = 0L) {
        repeat(seconds + 1) { i ->
            tracker.onLocationUpdated(
                LatLng(37.5665 + latOffset, 126.9780 + 0.0002 * i),
                startMillis + i * 1_000L
            )
        }
    }

    @Test
    fun `코스에서 벗어난 채로 8초 이상 달리면 이탈 알림이 뜬다`() {
        tracker.setRoute(straightRoute)

        // 첫 위치는 기준점으로만 쓰여 판정이 1초부터 시작되므로 9초 달린다. 약 55m 이탈.
        runAlongside(latOffset = 0.0005, seconds = 9)

        assertTrue(tracker.state.value.alert is RunAlert.OffRoute)
    }

    @Test
    fun `코스 위를 달리면 이탈 알림이 뜨지 않는다`() {
        tracker.setRoute(straightRoute)

        runAlongside(latOffset = 0.0001, seconds = 10) // 약 11m, GPS 오차 범위

        assertEquals(null, tracker.state.value.alert)
    }

    @Test
    fun `일시정지하면 떠 있던 이탈 알림이 사라진다`() {
        tracker.setRoute(straightRoute)
        runAlongside(latOffset = 0.0005, seconds = 9)
        assertTrue(tracker.state.value.alert is RunAlert.OffRoute)

        tracker.pause()

        assertEquals(null, tracker.state.value.alert)
    }

    @Test
    fun `목표 페이스보다 20퍼센트 넘게 느리게 10초 이상 달리면 페이스 저하 알림이 뜬다`() {
        tracker.setRoute(straightRoute)
        tracker.setTargetPace(30.0) // 초/km, 1초에 약 18m(약 56초/km)면 목표보다 훨씬 느림

        runAlongside(latOffset = 0.0, seconds = 12)

        assertTrue(tracker.state.value.alert is RunAlert.PaceDrop)
    }

    @Test
    fun `목표 페이스를 설정하지 않으면 느리게 달려도 페이스 저하 알림이 뜨지 않는다`() {
        tracker.setRoute(straightRoute)
        tracker.setTargetPace(null)

        runAlongside(latOffset = 0.0, seconds = 12)

        assertEquals(null, tracker.state.value.alert)
    }

    @Test
    fun `기준점이 틀려 튐이 연속되면 기준점을 새 위치로 옮기고 이후 이동은 정상 누적한다`() {
        tracker.onLocationUpdated(LatLng(37.5000, 126.9000), 0L) // 부정확한 첫 측위(실제 위치에서 수 km)
        // 실제 위치 근처에서 약 18m씩 이동 — 처음 3번은 틀린 기준점 대비 튐으로 거부되고, 3번째에 기준점이 옮겨진다
        repeat(8) { i ->
            tracker.onLocationUpdated(LatLng(37.5665, 126.9780 + 0.0002 * i), (i + 1) * 1_000L)
        }

        val distanceKm = tracker.state.value.distanceKm ?: 0.0
        assertTrue("기준점 이동 후 약 88m가 누적돼야 한다: $distanceKm", distanceKm in 0.05..0.15)
    }

    @Test
    fun `위치 갱신 한 번의 이동량이 작아도 계속 달리고 있으면 자동 일시정지되지 않는다`() {
        // 1초마다 약 2.8m(5'57" 페이스) 이동 — 한 번의 이동량은 움직임 임계값보다 작다
        repeat(61) { i ->
            tracker.onLocationUpdated(LatLng(37.5665 + 0.0000252 * i, 126.9780), i * 1_000L)
        }

        assertEquals(false, tracker.shouldAutoPause(60_000L))
    }

    @Test
    fun `제자리에서 GPS가 몇 미터씩 흔들리기만 하면 움직임으로 보지 않는다`() {
        repeat(21) { i ->
            val jitter = if (i % 2 == 0) 0.0 else 0.00003 // 약 3m 왕복
            tracker.onLocationUpdated(LatLng(37.5665 + jitter, 126.9780), i * 1_000L)
        }

        assertTrue(tracker.shouldAutoPause(20_000L))
    }

    @Test
    fun `달리는 동안에만 경과 시간이 올라간다`() {
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780), 0L)
        tracker.onSecondElapsed(1_000L)
        tracker.onSecondElapsed(2_000L)
        tracker.pause()
        tracker.onSecondElapsed(3_000L)

        assertEquals(2, tracker.state.value.elapsedSec)
    }

    @Test
    fun `무활동이 이어지면 1초 틱에서 자동 일시정지하고 true를 반환한다`() {
        tracker.onLocationUpdated(LatLng(37.5665, 126.9780), 0L)

        assertEquals(false, tracker.onSecondElapsed(19_000L))
        assertEquals(true, tracker.onSecondElapsed(20_000L))
        assertEquals(true, tracker.state.value.isPaused)
    }

    @Test
    fun `목표 페이스는 상태에 담겨 화면에 표시된다`() {
        tracker.setTargetPace(300.0)
        assertEquals(300.0, requireNotNull(tracker.state.value.targetPaceSecPerKm), 0.0)

        tracker.setTargetPace(null)
        assertEquals(null, tracker.state.value.targetPaceSecPerKm)
    }

    @Test
    fun `기준점을 옮긴 직후에는 옮기기 전 위치가 페이스 계산에 섞이지 않는다`() {
        tracker.onLocationUpdated(LatLng(37.5000, 126.9000), 0L) // 멀리 떨어진 부정확한 첫 측위
        repeat(8) { i ->
            tracker.onLocationUpdated(LatLng(37.5665, 126.9780 + 0.0000318 * i), (i + 1) * 1_000L) // 약 2.8m/s
        }

        val pace = requireNotNull(tracker.state.value.paceSecPerKm)
        assertTrue("5'57\" 근처여야 한다: $pace", pace in 300.0..420.0)
    }

    @Test
    fun `재개 직후 페이스에는 일시정지 중 이동분이 섞이지 않는다`() {
        repeat(6) { i -> tracker.onLocationUpdated(LatLng(37.5665, 126.9780 + 0.0000318 * i), i * 1_000L) }
        tracker.pause()
        tracker.onLocationUpdated(LatLng(37.5665, 126.9790), 7_000L) // 정지 중 약 60m 이동
        tracker.resume(8_000L)
        repeat(4) { i -> tracker.onLocationUpdated(LatLng(37.5665, 126.9790 + 0.0000318 * (i + 1)), 9_000L + i * 1_000L) }

        val pace = requireNotNull(tracker.state.value.paceSecPerKm)
        assertTrue("정지 중 이동이 섞이면 비정상적으로 빨라진다: $pace", pace in 300.0..420.0)
    }
}
