package com.example.runnect.presentation.draw

import android.content.ContentValues
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.databinding.ActivityDrawBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import timber.log.Timber

class DrawActivity : BindingActivity<ActivityDrawBinding>(R.layout.activity_draw),
    OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val touchList = mutableListOf<TouchLatLng>()
    private val markerList = mutableListOf<Marker>()

    val coords = mutableListOf(
        LatLng(37.5640984, 126.9712268), //이거는 처음 출발지 설정으로 메꾸기 가능
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //MapFragment 추가
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

//        // 시작 지점 설정
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497885, 127.027512))
//        naverMap.moveCamera(cameraUpdate)

        // 현재 위치 받아오기 버튼
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = false
//        currentLocationButton.map = naverMap

        //네이버 맵 sdk에 위치 정보 제공
        locationSource = FusedLocationSource(this@DrawActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        drawCourse()

    }


    private fun drawCourse() {
        val path = PathOverlay()

        //맵 터치 이벤트 관리
        naverMap.setOnMapClickListener { point, coord ->
            // 수신한 좌표값을 touchList에 추가
            touchList.add(
                TouchLatLng(
                    "${coord.latitude}".toDouble(),
                    "${coord.longitude}".toDouble()
                )
            )

            val marker = Marker()

            marker.position = LatLng(
                "${coord.latitude}".toDouble(),
                "${coord.longitude}".toDouble()
            )
            marker.anchor = PointF(0.5f, 0.5f)
            marker.icon = OverlayImage.fromResource(R.drawable.marker_line)
            marker.map = naverMap

            markerList.add(marker)

            Timber.tag(ContentValues.TAG).d("markerListSize : ${markerList.size}")
            Timber.tag(ContentValues.TAG).d("markerList : ${markerList}")


            // 경로선 list인 coords에 터치로 받아온 좌표값을 추가
            coords.add(LatLng("${coord.latitude}".toDouble(), "${coord.longitude}".toDouble()))

            // 경로선 그리기
            path.coords = coords

            Timber.tag(ContentValues.TAG).d("pat.coords : ${path.coords}")

            // 경로선 색상
            path.color = Color.parseColor("#593EEC")

            path.map = naverMap
        }

        binding.btnMarkerBack.setOnClickListener {
            if (touchList.size > 1) {
                touchList.removeLast()
                Timber.tag(ContentValues.TAG).d("markerList : ${markerList.size}")
                markerList.last().map = null
                markerList.removeLast()
                Timber.tag(ContentValues.TAG).d("markerList : ${markerList.size}")


                coords.removeLast()
                path.coords = coords
                path.map = naverMap
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }

        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { //권한 거부 시
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
