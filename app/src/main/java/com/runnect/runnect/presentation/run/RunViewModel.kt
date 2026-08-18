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

    /**
     * 위치가 갱신될 때마다 호출한다. 일시정지 중에는 누적하지 않되, lastLocation은
     * 갱신해서 재개 시 일시정지 이전 위치와의 거리가 한 번에 더해지지 않게 한다.
     * GPS 튐으로 보이는 비정상적으로 큰 한 번의 이동(MAX_PLAUSIBLE_JUMP_M 초과)도 무시한다.
     */
    fun onLocationUpdated(newLocation: LatLng) {
        val previous = lastLocation
        lastLocation = newLocation

        if (isPaused.value == true || previous == null) return

        val deltaM = previous.distanceTo(newLocation)
        if (deltaM > MAX_PLAUSIBLE_JUMP_M) return

        traveledDistanceM += deltaM
        traveledDistanceKm.value = (traveledDistanceM / 1000).round(1)
    }

    companion object {
        private const val MAX_PLAUSIBLE_JUMP_M = 50.0
    }
}
