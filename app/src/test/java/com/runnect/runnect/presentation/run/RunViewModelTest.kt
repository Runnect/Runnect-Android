package com.runnect.runnect.presentation.run

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.naver.maps.geometry.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RunViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RunViewModel

    @Before
    fun setUp() {
        viewModel = RunViewModel()
    }

    @Test
    fun `첫 위치 업데이트는 이전 위치가 없어서 거리를 누적하지 않는다`() {
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780))

        assertEquals(0.0, viewModel.traveledDistanceKm.value)
    }

    @Test
    fun `연속된 여러 위치 이동만큼 거리가 누적된다`() {
        val baseLat = 37.5665
        viewModel.onLocationUpdated(LatLng(baseLat, 126.9780))
        // 각 구간 약 22m씩(임계값 50m 이내), 5구간 합산 약 111m
        repeat(5) { i ->
            viewModel.onLocationUpdated(LatLng(baseLat + 0.0002 * (i + 1), 126.9780))
        }

        val distanceKm = viewModel.traveledDistanceKm.value ?: 0.0
        assertTrue("누적 거리가 0보다 커야 한다: $distanceKm", distanceKm > 0.0)
        assertTrue("누적 거리가 비정상적으로 크면 안 된다: $distanceKm", distanceKm < 0.3)
    }

    @Test
    fun `일시정지 중에는 이동해도 거리가 누적되지 않는다`() {
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780))
        viewModel.isPaused.value = true

        viewModel.onLocationUpdated(LatLng(37.5675, 126.9780))

        assertEquals(0.0, viewModel.traveledDistanceKm.value)
    }

    @Test
    fun `일시정지 해제 후에는 정지 중 이동분을 제외하고 재개 이후 이동만 누적된다`() {
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780))
        viewModel.isPaused.value = true
        viewModel.onLocationUpdated(LatLng(37.5715, 126.9780)) // 일시정지 중 약 555m 이동 — 누적되면 안 됨
        viewModel.isPaused.value = false

        viewModel.onLocationUpdated(LatLng(37.5716, 126.9780)) // 재개 후 약 11m만 실제 이동

        val distanceAfterResume = viewModel.traveledDistanceKm.value ?: 0.0
        assertTrue(
            "재개 직후 거리는 일시정지 중 이동분(약 555m)을 포함하면 안 된다: $distanceAfterResume",
            distanceAfterResume < 0.1
        )
    }

    @Test
    fun `GPS 튐으로 보이는 비정상적으로 큰 한 번의 이동은 무시한다`() {
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780))
        viewModel.onLocationUpdated(LatLng(37.5765, 126.9780)) // 약 1.1km, 임계값(50m) 훨씬 초과

        assertEquals(0.0, viewModel.traveledDistanceKm.value)
    }

    @Test
    fun `충분히 움직이지 않으면 페이스는 null이다`() {
        val now = 0L
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780), now)

        assertEquals(null, viewModel.currentPaceSecPerKm.value)
    }

    @Test
    fun `최근 구간 이동 거리와 시간으로 페이스가 계산된다`() {
        val now = 0L
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780), now)
        // 페이스 샘플은 두 번째 위치 업데이트부터 기록되므로, 최소 2개 샘플을 쌓기 위해 3번 갱신한다.
        viewModel.onLocationUpdated(LatLng(37.5667, 126.9780), now + 5_000)
        viewModel.onLocationUpdated(LatLng(37.5669, 126.9780), now + 10_000)

        val pace = viewModel.currentPaceSecPerKm.value
        assertTrue("페이스가 계산되어야 한다: $pace", pace != null && pace > 0)
    }

    @Test
    fun `일시정지 중에는 페이스가 갱신되지 않는다`() {
        val now = 0L
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780), now)
        viewModel.isPaused.value = true

        viewModel.onLocationUpdated(LatLng(37.5675, 126.9780), now + 10_000)

        assertEquals(null, viewModel.currentPaceSecPerKm.value)
    }

    @Test
    fun `무활동 시간이 임계값을 넘으면 자동 일시정지가 필요하다`() {
        val now = 0L
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780), now)

        assertTrue(viewModel.shouldAutoPause(now + 60_000))
    }

    @Test
    fun `무활동 시간이 임계값 미만이면 자동 일시정지가 필요없다`() {
        val now = 0L
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780), now)

        assertEquals(false, viewModel.shouldAutoPause(now + 30_000))
    }

    @Test
    fun `이미 일시정지 상태면 자동 일시정지를 다시 트리거하지 않는다`() {
        val now = 0L
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780), now)
        viewModel.isPaused.value = true

        assertEquals(false, viewModel.shouldAutoPause(now + 60_000))
    }

    @Test
    fun `수동 재개 직후에는 무활동 타이머가 초기화되어 바로 자동 일시정지되지 않는다`() {
        val now = 0L
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780), now)

        viewModel.onManualResume(now + 60_000)

        assertEquals(false, viewModel.shouldAutoPause(now + 60_000 + 30_000))
    }

    @Test
    fun `움직임이 감지되면 무활동 타이머가 갱신된다`() {
        val now = 0L
        viewModel.onLocationUpdated(LatLng(37.5665, 126.9780), now)
        // 임계값(5m) 이상 이동 -> 무활동 타이머 갱신
        viewModel.onLocationUpdated(LatLng(37.56655, 126.9780), now + 30_000)

        assertEquals(false, viewModel.shouldAutoPause(now + 30_000 + 59_000))
        assertTrue(viewModel.shouldAutoPause(now + 30_000 + 60_000))
    }
}
