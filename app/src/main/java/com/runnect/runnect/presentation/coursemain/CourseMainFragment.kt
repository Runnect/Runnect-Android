package com.runnect.runnect.presentation.coursemain

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentCourseMainBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.search.SearchActivity


class CourseMainFragment :
    BindingFragment<FragmentCourseMainBinding>(R.layout.fragment_course_main),
    OnMapReadyCallback {


    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var currentLocation: LatLng = LatLng(37.52901832956373, 126.9136196847032) //국회의사당 좌표

    private lateinit var fusedLocation: FusedLocationProviderClient//현재 위치 반환 객체 변수

    private var mainActivity: MainActivity? = null

    var selectionTracker: SelectionTracker<Long>? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        getCurrentLocation()
        drawCourseButton()
    }


    private fun init() {
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
        requestPermission()
    }

    private fun initView() {

        //MapFragment 추가
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }


    private fun getCurrentLocation() {
        binding.btnCurrentLocation.setOnClickListener {
            cameraUpdate(currentLocation)
        }
    }

    private fun drawCourseButton() {
        binding.btnDraw.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
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
        locationSource = FusedLocationSource(this,
            LOCATION_PERMISSION_REQUEST_CODE)
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

        //현위치 커스텀 이미지
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.icon = OverlayImage.fromResource(R.drawable.current_location)
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

    private fun cameraUpdate(location: LatLng) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}