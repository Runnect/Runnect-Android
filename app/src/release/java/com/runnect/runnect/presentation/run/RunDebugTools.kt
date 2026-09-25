package com.runnect.runnect.presentation.run

import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng

/** release 빌드에서는 아무것도 하지 않는다. 실제 구현(GPS 시뮬레이터)은 debug 소스셋에 있다. */
object RunDebugTools {
    fun attach(activity: AppCompatActivity, route: List<LatLng>, controller: () -> RunController?) = Unit

    fun onServiceTick(state: RunTrackingState) = Unit
}
