package com.example.runnect.presentation.draw

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
    private val touchList = mutableListOf<HouseModel>()

    val path = PathOverlay()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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


        getTouchState()

    }

    private fun getTouchState() {
        val coords = mutableListOf(
            LatLng(37.5640984, 126.9712268), //이거는 처음 출발지 설정으로 메꾸기 가능
            LatLng(37.5651279, 126.9767904),
//                LatLng("${coord.latitude}".toDouble(), "${coord.longitude}".toDouble())
        )
        naverMap.setOnMapClickListener { point, coord ->
            Toast.makeText(
                this, "${coord.latitude}, ${coord.longitude}",
                Toast.LENGTH_SHORT
            ).show()
            touchList.add(HouseModel("${coord.latitude}".toDouble(),"${coord.longitude}".toDouble()))


            updateMarker(touchList)
            Timber.tag(ContentValues.TAG).d("listSize : ${touchList.size}")
            Timber.tag(ContentValues.TAG).d("touchList : ${touchList}")


// touchList는 잘 늘어나는데 LatLng 이게 요소 3개에서 더 안 늘어나.
            // 내 생각엔 이게 터치 될 때마다 mutableListOf가 고정값 2개로 다시 초기화되고 직전에 터치한 게 바로 추가돼서
            // 계속 3개가 유지되는듯. 그렇다면 저 coords를 터치할 때마다 갱신일어나지 않게, 갱신은 그냥 add만 일어나게끔 밖으로 빼주면 될듯?
//와 미친 해결했다.

//            path.coords = coords
            coords.add(LatLng("${coord.latitude}".toDouble(), "${coord.longitude}".toDouble()))
            path.coords = coords



            Timber.tag(ContentValues.TAG).d("pat.coords : ${path.coords}")

            path.color = Color.parseColor("#593EEC")
            path.map = naverMap
        }
    }

//지금 무슨 상황이냐면 로그 찍어보니까 터치한 좌표가 path.coords에 찍히긴 함
    //근데 이게 누적이 안 돼. ui 상으로 마커는 늘어나는데 정작 내가 관리하는 list 안에는
    //갯수가 안 늘어나고 있던 상황? -> 아닌데,, touchList는 늘어나고 있는데
    //touchList는 늘어나는데 지금 path.coords가 안 늘어나고 있음.

//    private fun pathOverlay(touchList: List<HouseModel>){
//        touchList.forEach { house ->
//
//            path.coords = mutableListOf(
//                LatLng(37.5640984, 126.9712268),
//                LatLng(37.5651279, 126.9767904),
//
//            )
//            path.coords.add(LatLng(house.lat, house.lng))
//            Timber.tag(ContentValues.TAG).d("들어갔나? : ${LatLng(house.lat, house.lng)}")
//            Timber.tag(ContentValues.TAG).d("pat.coords : ${path.coords}")
//
//            path.color = Color.parseColor("#593EEC")
//            path.map = naverMap
//        }
//
//    }

    private fun updateMarker(touchList: List<HouseModel>) {

        //Data 클래스로 묶어놓은 값들 x,y 하나씩 어떻게 빼와?
        // forEach쓰니까 임의의 house라는 게 생겼고 하려던 게 가능했는데
        // forEach 안 쓰고도 빼오는 방법은 없나?
        touchList.forEach{
            house ->
            val marker = Marker()
            marker.position = LatLng(house.lat, house.lng)
            marker.icon = OverlayImage.fromResource(R.drawable.rennect_marker)
            marker.map = naverMap
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
