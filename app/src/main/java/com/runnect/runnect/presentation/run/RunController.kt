package com.runnect.runnect.presentation.run

/** 러닝 진행을 조작하는 창구. 러닝 서비스(TimerService)가 구현하고, 화면과 debug 도구가 이걸로 조작한다. */
interface RunController {
    fun pause()
    fun resume()
    fun setTargetPace(paceSecPerKm: Double?)
}
