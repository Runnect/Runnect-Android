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
}
