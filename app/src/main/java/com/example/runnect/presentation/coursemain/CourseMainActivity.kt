package com.example.runnect.presentation.coursemain

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.databinding.ActivityCourseMainBinding
import com.example.runnect.presentation.search.SearchActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource

class CourseMainActivity :
    BindingActivity<ActivityCourseMainBinding>(R.layout.activity_course_main),
    OnMapReadyCallback {


    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var currentLocation: LatLng = LatLng(37.52901832956373, 126.9136196847032) //국회의사당 좌표

    private lateinit var fusedLocation: FusedLocationProviderClient//현재 위치 반환 객체 변수


    private fun initView() {

        //MapFragment 추가
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this) //지도 객체 얻어오기
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        getCurrentLocation()
        drawCourseButton()
    }

    private fun init() {
        fusedLocation = LocationServices.getFusedLocationProviderClient(this) //
        requestPermission()
//        initView() //지도 뷰 표시
    }

    private fun requestPermission() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() { //요청 승인 시
                    initView() //지도 뷰 표시
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) { //요청 거부 시
                    naverMap.locationTrackingMode = LocationTrackingMode.None
                    onDestroy() //앱 종료
                }
            })
            .setRationaleTitle("위치권한 요청")
            .setRationaleMessage("현재 위치로 이동하기 위해 위치 권한이 필요합니다.")
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }

    private fun getCurrentLocation() {
        binding.btnCurrentLocation.setOnClickListener {
            cameraUpdate(currentLocation)
        }
    }

    private fun drawCourseButton() {
        binding.btnDraw.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onMapReady(map: NaverMap) {

        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        map.locationSource = locationSource
        map.locationTrackingMode = LocationTrackingMode.Follow //위치추적 모드 Follow


        //네이버 맵 sdk에 위치 정보 제공
        locationSource =
            FusedLocationSource(this@CourseMainActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        naverMap.addOnLocationChangeListener { location ->
            currentLocation = LatLng(location.latitude, location.longitude)
            map.locationOverlay.run { //현재 위치 마커
                isVisible = true //현재 위치 마커 가시성(default = false)
                position = LatLng(currentLocation.latitude, currentLocation.longitude)
            }
        }

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false
    }


    private fun cameraUpdate(location: LatLng) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
